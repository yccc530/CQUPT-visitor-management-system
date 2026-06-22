package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.cqupt.visitor.dto.CurrentUserResponse;
import edu.cqupt.visitor.dto.LoginRequest;
import edu.cqupt.visitor.dto.LoginResponse;
import edu.cqupt.visitor.dto.MenuPermissionResponse;
import edu.cqupt.visitor.entity.SysPermission;
import edu.cqupt.visitor.entity.SysRole;
import edu.cqupt.visitor.entity.SysRolePermission;
import edu.cqupt.visitor.entity.SysUser;
import edu.cqupt.visitor.entity.SysUserRole;
import edu.cqupt.visitor.exception.BusinessException;
import edu.cqupt.visitor.security.AuthContext;
import edu.cqupt.visitor.security.CurrentUser;
import edu.cqupt.visitor.security.JwtTokenProvider;
import edu.cqupt.visitor.security.RevokedTokenStore;
import edu.cqupt.visitor.service.AuthService;
import edu.cqupt.visitor.service.SysPermissionService;
import edu.cqupt.visitor.service.SysRolePermissionService;
import edu.cqupt.visitor.service.SysRoleService;
import edu.cqupt.visitor.service.SysUserRoleService;
import edu.cqupt.visitor.service.SysUserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;
    private final SysRoleService sysRoleService;
    private final SysRolePermissionService sysRolePermissionService;
    private final SysPermissionService sysPermissionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RevokedTokenStore revokedTokenStore;

    public AuthServiceImpl(SysUserService sysUserService,
                           SysUserRoleService sysUserRoleService,
                           SysRoleService sysRoleService,
                           SysRolePermissionService sysRolePermissionService,
                           SysPermissionService sysPermissionService,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           RevokedTokenStore revokedTokenStore) {
        this.sysUserService = sysUserService;
        this.sysUserRoleService = sysUserRoleService;
        this.sysRoleService = sysRoleService;
        this.sysRolePermissionService = sysRolePermissionService;
        this.sysPermissionService = sysPermissionService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.revokedTokenStore = revokedTokenStore;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername())
                .eq(SysUser::getDeleted, 0), false);
        if (user == null || !"ENABLED".equals(user.getStatus())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        user.setLastLoginTime(LocalDateTime.now());
        sysUserService.updateById(user);

        CurrentUser currentUser = buildCurrentUser(user);
        String token = jwtTokenProvider.generateToken(currentUser);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpiresIn(jwtTokenProvider.getExpirationSeconds());
        response.setUser(toCurrentUserResponse(currentUser));
        return response;
    }

    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            revokedTokenStore.revoke(token, jwtTokenProvider.getExpiration(token));
        }
    }

    @Override
    public CurrentUser loadCurrentUser(Long userId) {
        SysUser user = sysUserService.getById(userId);
        if (user == null || !"ENABLED".equals(user.getStatus())) {
            throw new BusinessException(401, "用户不存在或已禁用");
        }
        return buildCurrentUser(user);
    }

    @Override
    public CurrentUserResponse getCurrentUserInfo() {
        return toCurrentUserResponse(AuthContext.currentUser());
    }

    @Override
    public List<MenuPermissionResponse> getCurrentUserMenus() {
        CurrentUser currentUser = AuthContext.currentUser();
        List<SysPermission> permissions = listPermissions(currentUser.getId()).stream()
                .filter(permission -> "MENU".equals(permission.getPermissionType()))
                .sorted(Comparator.comparing(SysPermission::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(SysPermission::getId))
                .toList();

        Map<Long, MenuPermissionResponse> nodeMap = new LinkedHashMap<>();
        for (SysPermission permission : permissions) {
            MenuPermissionResponse node = new MenuPermissionResponse();
            node.setId(permission.getId());
            node.setParentId(permission.getParentId());
            node.setPermissionCode(permission.getPermissionCode());
            node.setPermissionName(permission.getPermissionName());
            node.setRoutePath(permission.getRoutePath());
            node.setComponentPath(permission.getComponentPath());
            node.setSortOrder(permission.getSortOrder());
            nodeMap.put(node.getId(), node);
        }

        List<MenuPermissionResponse> roots = new ArrayList<>();
        for (MenuPermissionResponse node : nodeMap.values()) {
            if (node.getParentId() != null && nodeMap.containsKey(node.getParentId())) {
                nodeMap.get(node.getParentId()).getChildren().add(node);
            } else {
                roots.add(node);
            }
        }
        return roots;
    }

    private CurrentUser buildCurrentUser(SysUser user) {
        List<SysRole> roles = listRoles(user.getId());
        List<SysPermission> permissions = listPermissions(user.getId());
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).distinct().toList();
        List<String> permissionCodes = permissions.stream().map(SysPermission::getPermissionCode).distinct().toList();
        return new CurrentUser(user.getId(), user.getUsername(), user.getPasswordHash(), user.getRealName(),
                user.getPhone(), user.getEmail(), user.getDepartmentId(), user.getUserType(), user.getStatus(),
                roleCodes, permissionCodes);
    }

    private List<SysRole> listRoles(Long userId) {
        List<SysUserRole> userRoles = sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getDeleted, 0));
        Set<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return sysRoleService.list(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getId, roleIds)
                .eq(SysRole::getStatus, "ENABLED")
                .eq(SysRole::getDeleted, 0));
    }

    private List<SysPermission> listPermissions(Long userId) {
        List<SysRole> roles = listRoles(userId);
        Set<Long> roleIds = roles.stream().map(SysRole::getId).collect(Collectors.toSet());
        if (roleIds.isEmpty()) {
            return List.of();
        }
        List<SysRolePermission> rolePermissions = sysRolePermissionService.list(new LambdaQueryWrapper<SysRolePermission>()
                .in(SysRolePermission::getRoleId, roleIds)
                .eq(SysRolePermission::getDeleted, 0));
        Set<Long> permissionIds = rolePermissions.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toSet());
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        return sysPermissionService.list(new LambdaQueryWrapper<SysPermission>()
                .in(SysPermission::getId, permissionIds)
                .eq(SysPermission::getStatus, "ENABLED")
                .eq(SysPermission::getDeleted, 0));
    }

    private CurrentUserResponse toCurrentUserResponse(CurrentUser currentUser) {
        CurrentUserResponse response = new CurrentUserResponse();
        response.setId(currentUser.getId());
        response.setUsername(currentUser.getUsername());
        response.setRealName(currentUser.getRealName());
        response.setPhone(currentUser.getPhone());
        response.setEmail(currentUser.getEmail());
        response.setDepartmentId(currentUser.getDepartmentId());
        response.setUserType(currentUser.getUserType());
        response.setRoles(currentUser.getRoles());
        response.setPermissions(currentUser.getPermissions());
        return response;
    }
}