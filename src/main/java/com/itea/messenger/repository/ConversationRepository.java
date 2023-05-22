package com.itea.messenger.repository;

import com.itea.messenger.entities.conversation.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {
    Optional<ConversationEntity> findById(long id);
}