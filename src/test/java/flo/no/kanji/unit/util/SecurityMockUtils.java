package flo.no.kanji.unit.util;

import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

public class SecurityMockUtils {

    public static void mockAuthentication() {
        var jwt = mock(Jwt.class);
        lenient().when(jwt.getClaimAsString(eq("sub"))).thenReturn("sub");
        var authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
