package edu.cqupt.visitor.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CurrentUser implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String realName;
    private final String phone;
    private final String email;
    private final Long departmentId;
    private final String userType;
    private final String status;
    private final List<String> roles;
    private final List<String> permissions;

    public CurrentUser(Long id, String username, String password, String realName, String phone, String email,
                       Long departmentId, String userType, String status,
                       List<String> roles, List<String> permissions) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.phone = phone;
        this.email = email;
        this.departmentId = departmentId;
        this.userType = userType;
        this.status = status;
        this.roles = roles == null ? List.of() : roles;
        this.permissions = permissions == null ? List.of() : permissions;
    }

    public Long getId() {
        return id;
    }

    public String getRealName() {
        return realName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getUserType() {
        return userType;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public boolean hasRole(String roleCode) {
        return roles.contains(roleCode);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return "ENABLED".equals(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ENABLED".equals(status);
    }
}