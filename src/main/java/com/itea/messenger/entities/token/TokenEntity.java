package com.itea.messenger.entities.token;

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
@Table(name = "tokens")
public class TokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String token;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}