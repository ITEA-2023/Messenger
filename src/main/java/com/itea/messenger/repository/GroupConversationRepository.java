package com.itea.messenger.repository;

import com.itea.messenger.entities.conversation.ConversationEntity;
import com.itea.messenger.entities.conversation.GroupConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupConversationRepository extends JpaRepository<GroupConversationEntity, Long> {
    GroupConversationEntity findByConversationEntity(ConversationEntity conversationEntity);
}