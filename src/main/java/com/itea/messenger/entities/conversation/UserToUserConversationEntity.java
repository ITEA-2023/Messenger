package com.itea.messenger.entities.conversation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_to_user_conversations")
public class UserToUserConversationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String firstUserConversationDisplayName;
    @Column(nullable = false)
    private String secondUserConversationDisplayName;

    @OneToOne
    @JoinColumn(name = "conversation_id")
    private ConversationEntity conversationEntity;
}