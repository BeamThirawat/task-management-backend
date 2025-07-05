package com.example.taskmanagement.security;

import com.example.taskmanagement.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final JwtUtil jwtUtil;

    @Value("${app.oauth2.authorized-redirect-uris}")
    private List<String> authorizedRedirectUris;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        logger.info("#### OAuth2 Authen Success Active ####");
        Object principal = authentication.getPrincipal();
        logger.debug("Authentication principal: {}", principal);

        String email;
        logger.debug("Check principal type");
        if (principal instanceof OidcUser oidcUser) {
            email = oidcUser.getAttribute("email");
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }

        // create Token
        logger.info("Create JWT token for email: {}", email);
        String token = jwtUtil.generateToken(email);

        // add token to cookie
        logger.info("Add JWT token to cookie");
        Cookie jwtCookie = new Cookie("token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60); // 1 day
        response.addCookie(jwtCookie);

        // Redirect to frontend
        logger.info("Redirect to frontend");
        String targetUrl = determineTargetUrl(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(HttpServletRequest request) {
        String redirectUri = request.getParameter("redirect_uri");

        if (redirectUri != null && isAuthorizedRedirectUri(redirectUri)) {
            return redirectUri;
        }

        // Default redirect URL
        return "http://localhost:4200/dashboard";
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        return authorizedRedirectUris.contains(uri);
    }
}
