package flo.no.kanji.business.service.impl;

import flo.no.kanji.business.service.UserService;
import flo.no.kanji.integration.entity.UserEntity;
import flo.no.kanji.integration.repository.UserRepository;
import flo.no.kanji.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void createOrUpdateUser(String sub) {
        var user = userRepository.findBySub(sub);
        if (user == null) {
            user = new UserEntity(sub);
        }
        user.setLastConnected(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public UserEntity getCurrentUser() {
        return userRepository.findBySub(AuthUtils.getUserSub());
    }
}
