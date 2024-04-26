package flo.no.kanji.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class AuthUtils {

    public static String getUserSub() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var token = (Jwt) auth.getPrincipal();
        return token.getClaimAsString("sub");
    }
}
