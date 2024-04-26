package flo.no.kanji.business.service;

import flo.no.kanji.integration.entity.UserEntity;

public interface UserService {

    void createOrUpdateUser(final String sub);

    UserEntity getCurrentUser();
}
