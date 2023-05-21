package com.itea.messenger.repository;

import com.itea.messenger.entities.message.MessageStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatusEntity, Long> {
}