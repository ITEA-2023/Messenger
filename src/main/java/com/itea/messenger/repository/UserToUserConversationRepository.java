package com.itea.messenger.repository;

import com.itea.messenger.entities.conversation.ConversationEntity;
import com.itea.messenger.entities.conversation.UserToUserConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserToUserConversationRepository extends JpaRepository<UserToUserConversationEntity, Long> {
    UserToUserConversationEntity findByConversationEntity(ConversationEntity conversationEntity);
}