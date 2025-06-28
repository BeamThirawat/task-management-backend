package com.example.taskmanagement.security;

import com.example.taskmanagement.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${app.oauth2.authorized-redirect-uris}")
    private List<String> authorizedRedirectUris;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // casting OAuth2User => CustomOAuth2User
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

        // create Token
        String token = jwtUtil.generateToken(oauthUser.getUser().getEmail());

        // add token to cookie
        Cookie jwtCookie = new Cookie("token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(jwtCookie);

        // Redirect to frontend
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
