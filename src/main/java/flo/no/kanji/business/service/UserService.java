package flo.no.kanji.business.service;

import flo.no.kanji.integration.entity.UserEntity;

public interface UserService {

    UserEntity createOrGetBySub(final String sub);
}
