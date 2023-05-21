package com.itea.messenger.repository;

import com.itea.messenger.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<UserEntity> findById(long id);
    void removeById(long id);

    @Query("select u from UserEntity u join u.userConversations c where c.id = :conversationId")
    List<UserEntity> findAllUserEntitiesByConversationEntityId(@Param("conversationId") long conversationId);
}