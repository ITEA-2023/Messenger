package com.itea.messenger.entities.message;

import com.itea.messenger.entities.conversation.ConversationEntity;
import com.itea.messenger.entities.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "sender_id")
    private UserEntity userEntity;

    @OneToOne
    @JoinColumn(name = "conversation_id")
    private ConversationEntity conversationEntity;

    @Column(nullable = false)
    private String messageBody;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}