package flo.no.kanji.business.service.impl;

import flo.no.kanji.business.service.UserService;
import flo.no.kanji.integration.entity.UserEntity;
import flo.no.kanji.integration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity createOrGetBySub(String sub) {
        var user = userRepository.findBySub(sub)
                .orElseGet(() -> new UserEntity(sub));
        user.setLastConnected(LocalDateTime.now());
        return userRepository.save(user);
    }
}
