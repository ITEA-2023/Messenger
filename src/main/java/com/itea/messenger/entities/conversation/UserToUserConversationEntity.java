package com.itea.messenger.entities.conversation;

import com.itea.messenger.entities.user.UserEntity;
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

    @OneToOne
    @JoinColumn(name = "first_user_id")
    private UserEntity firstUserEntity;

    @OneToOne
    @JoinColumn(name = "second_user_d")
    private UserEntity secondUserEntity;

    @OneToOne
    @JoinColumn(name = "conversation_id")
    private ConversationEntity conversationEntity;
}