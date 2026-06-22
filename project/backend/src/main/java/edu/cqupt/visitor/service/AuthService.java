package edu.cqupt.visitor.service;

import edu.cqupt.visitor.dto.CurrentUserResponse;
import edu.cqupt.visitor.dto.LoginRequest;
import edu.cqupt.visitor.dto.LoginResponse;
import edu.cqupt.visitor.dto.MenuPermissionResponse;
import edu.cqupt.visitor.security.CurrentUser;
import java.util.List;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void logout(String token);

    CurrentUser loadCurrentUser(Long userId);

    CurrentUserResponse getCurrentUserInfo();

    List<MenuPermissionResponse> getCurrentUserMenus();
}