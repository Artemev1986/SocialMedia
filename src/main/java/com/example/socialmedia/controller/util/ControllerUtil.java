package com.example.socialmedia.controller.util;

import com.example.socialmedia.exception.ForbiddenException;
import com.example.socialmedia.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ControllerUtil {
    private final JwtProvider jwtProvider;

    public void validateTokenAndEmail(String email, String token) {
        String emailDec = email.contains("%40") ? email.replace("%40" , "@"): email;
        String emailJwt = jwtProvider.getEmailFromToken(token.substring(7));
        if (!emailDec.equals(emailJwt)) {
            throw new ForbiddenException("The email from the requestBody does not match the email from the token");
        }
    }
}
