package com.itea.messenger.repository;

import com.itea.messenger.entities.token.TokenEntity;
import com.itea.messenger.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    void removeByUserEntity(UserEntity userEntity);
    void removeByToken(String token);
    boolean existsByToken(String token);
}