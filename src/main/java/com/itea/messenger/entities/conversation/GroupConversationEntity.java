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
@Table(name = "group_conversations")
public class GroupConversationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String groupName;

    @OneToOne
    @JoinColumn(name = "group_creator_id")
    private UserEntity creator;

    @OneToOne
    @JoinColumn(name = "conversation_id")
    private ConversationEntity conversationEntity;
}