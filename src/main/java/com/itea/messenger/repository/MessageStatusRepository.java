package com.itea.messenger.repository;

import com.itea.messenger.entities.message.MessageEntity;
import com.itea.messenger.entities.message.MessageStatus;
import com.itea.messenger.entities.message.MessageStatusEntity;
import com.itea.messenger.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatusEntity, Long> {
    List<MessageStatusEntity> findAllByUserEntityAndMessageStatus(UserEntity userEntity, MessageStatus messageStatus);
    MessageStatusEntity findByMessageEntityAndUserEntity(MessageEntity messageEntity, UserEntity userEntity);
}