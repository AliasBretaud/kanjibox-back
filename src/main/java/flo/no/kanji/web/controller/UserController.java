package flo.no.kanji.web.controller;

import flo.no.kanji.business.service.UserService;
import flo.no.kanji.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public void createUser() {
        var sub = AuthUtils.getUserSub();
        userService.createOrUpdateUser(sub);
    }
}
