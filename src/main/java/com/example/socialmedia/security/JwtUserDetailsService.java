package com.example.socialmedia.security;

import com.example.socialmedia.entity.User;
import com.example.socialmedia.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public JwtUserDetails loadUserByUsername(String email) {
        log.debug("finding user by email: {}", email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("User named " + email + " not found");
        }
        log.debug("user with email {} found", email);
        return JwtUserDetails.fromUserToJwtUserDetails(user);
    }
}