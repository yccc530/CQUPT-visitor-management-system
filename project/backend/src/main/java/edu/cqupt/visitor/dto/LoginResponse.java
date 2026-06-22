package edu.cqupt.visitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登录响应")
public class LoginResponse {

    @Schema(description = "JWT Token")
    private String token;

    @Schema(description = "Token 类型")
    private String tokenType = "Bearer";

    @Schema(description = "过期秒数")
    private Long expiresIn;

    @Schema(description = "当前用户信息")
    private CurrentUserResponse user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public CurrentUserResponse getUser() {
        return user;
    }

    public void setUser(CurrentUserResponse user) {
        this.user = user;
    }
}