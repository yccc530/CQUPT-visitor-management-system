package edu.cqupt.visitor.controller;

import edu.cqupt.visitor.common.ApiResponse;
import edu.cqupt.visitor.dto.CurrentUserResponse;
import edu.cqupt.visitor.dto.LoginRequest;
import edu.cqupt.visitor.dto.LoginResponse;
import edu.cqupt.visitor.dto.MenuPermissionResponse;
import edu.cqupt.visitor.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "登录认证与权限")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "登录并获取 JWT Token")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("登录成功", authService.login(request));
    }

    @Operation(summary = "退出登录并撤销当前 Token")
    @PostMapping("/logout")
    public ApiResponse<Boolean> logout(HttpServletRequest request) {
        authService.logout(resolveToken(request));
        return ApiResponse.ok("退出登录成功", true);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> currentUser() {
        return ApiResponse.ok(authService.getCurrentUserInfo());
    }

    @Operation(summary = "获取当前用户菜单权限")
    @GetMapping("/menus")
    public ApiResponse<List<MenuPermissionResponse>> menus() {
        return ApiResponse.ok(authService.getCurrentUserMenus());
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}