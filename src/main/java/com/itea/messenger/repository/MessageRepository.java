package com.itea.messenger.repository;

import com.itea.messenger.entities.conversation.ConversationEntity;
import com.itea.messenger.entities.message.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findAllByConversationEntity(ConversationEntity conversationEntity);
}