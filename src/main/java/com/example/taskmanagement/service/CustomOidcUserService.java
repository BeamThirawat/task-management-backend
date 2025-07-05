package com.example.taskmanagement.service;

import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.security.CustomOidcUser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOidcUserService.class);

    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("### Custom OIDC User Service Active ###");

        OidcUser oidcUser = super.loadUser(userRequest);

        // get Data from Google
        logger.info("### Get data from Google");
        Map<String, Object> attributes = oidcUser.getAttributes();
        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // find or create user in db
        logger.info("### Find or create user in DB");
        User user = findOrCreateUser(googleId, email, name);

        logger.info("### Return CustomOidcUser with User entity");
        return new CustomOidcUser(oidcUser, user);
    }

    private User findOrCreateUser(String googleId, String email, String name) {
        logger.debug("Find by googleId: {}", googleId);
        Optional<User> userByGoogle = userRepository.findByGoogleId(googleId);
        if (userByGoogle.isPresent()) {
            logger.debug("User found by Google ID");
            return userByGoogle.get();
        }

        logger.debug("Find by email: {}", email);
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            User existingUser = userByEmail.get();
            logger.debug("User found by email. Checking if Google ID needs to be set...");
            if (existingUser.getGoogleId() == null) {
                existingUser.setGoogleId(googleId);
                logger.debug("Google ID set for existing user");
                return userRepository.save(existingUser);
            }
            return existingUser;
        }

        // Create new user
        logger.info("Creating new user: {}", email);
        User newUser = new User(email, name, googleId);
        return userRepository.save(newUser);
    }
}
