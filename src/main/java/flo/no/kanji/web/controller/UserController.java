package flo.no.kanji.web.controller;

import flo.no.kanji.business.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for managing user accounts")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create or get user", description = "Initializes the user profile in the database based on the JWT 'sub' claim.")
    public void createUser(@Parameter(hidden = true) JwtAuthenticationToken token) {
        userService.createOrGetBySub(token.getName());
    }
}
