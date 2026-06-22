package edu.cqupt.visitor.security;

import edu.cqupt.visitor.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RevokedTokenStore revokedTokenStore;
    private final AuthService authService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   RevokedTokenStore revokedTokenStore,
                                   AuthService authService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.revokedTokenStore = revokedTokenStore;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token) && !revokedTokenStore.isRevoked(token)) {
            Long userId = jwtTokenProvider.getUserId(token);
            CurrentUser currentUser = authService.loadCurrentUser(userId);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    currentUser, token, currentUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}