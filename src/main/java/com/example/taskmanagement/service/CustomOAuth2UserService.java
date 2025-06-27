package com.example.taskmanagement.service;

import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.security.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // get Data from google
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // find user in db
        User user = findOrCreateUser(googleId, email, name);

        // Create CustomOAuth2User to have User entity
        return new CustomOAuth2User(oAuth2User, user);
    }

    private User findOrCreateUser(String googleId, String email, String name) {
        // find by googleId
        Optional<User> userByGoogle = userRepository.findByGoogleId(googleId);
        if (userByGoogle.isPresent()) {
            return userByGoogle.get();
        }

        // find by email
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            User existingUser = userByEmail.get();
            // If you already have an account but don't have a Google ID, add a Google ID.
            if (existingUser.getGoogleId() == null) {
                existingUser.setGoogleId(googleId);
                return userRepository.save(existingUser);
            }
            return existingUser;
        }

        // create new user
        User newUser = new User(email, name, googleId);
        return userRepository.save(newUser);
    }

}
