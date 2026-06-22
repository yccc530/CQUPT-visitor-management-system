package edu.cqupt.visitor.security;

import edu.cqupt.visitor.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthContext {

    private AuthContext() {
    }

    public static CurrentUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser currentUser)) {
            throw new BusinessException(401, "用户未登录或登录状态已失效");
        }
        return currentUser;
    }

    public static boolean hasRole(String roleCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser currentUser)) {
            return false;
        }
        return currentUser.hasRole(roleCode);
    }
}