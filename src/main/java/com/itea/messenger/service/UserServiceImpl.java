package com.itea.messenger.service;

import com.itea.messenger.dto.ModificationStatus;
import com.itea.messenger.dto.requests.UserAuthenticationAndDeletionRequest;
import com.itea.messenger.dto.requests.UserModificationRequest;
import com.itea.messenger.dto.responses.UserDeletionResponse;
import com.itea.messenger.dto.responses.UserModificationResponse;
import com.itea.messenger.entities.user.UserEntity;
import com.itea.messenger.repository.TokenRepository;
import com.itea.messenger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDeletionResponse delete(long userId, UserAuthenticationAndDeletionRequest userAuthenticationAndDeletionRequest) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Unable to find user. User with id: " + userId + " doesn't exist"));

        if (!userEntity.getUsername().equals(userAuthenticationAndDeletionRequest.getUsername())) {
            throw new UsernameNotFoundException("Unable to find user. User with username: " +
                    userAuthenticationAndDeletionRequest.getUsername() + " doesn't exist");
        }

        if (!passwordEncoder.matches(userAuthenticationAndDeletionRequest.getPassword(), userEntity.getPassword())) {
            throw new RuntimeException("Unable to delete user. Invalid password");
        }

        tokenRepository.removeByUserEntity(userEntity);
        userRepository.removeById(userEntity.getId());

        return UserDeletionResponse.builder()
                .username(userEntity.getUsername())
                .modificationType(ModificationStatus.DELETED)
                .build();
    }

    @Override
    public UserModificationResponse update(long userId, UserModificationRequest userModificationRequest) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Unable to find user. User with id: " + userId + " doesn't exist"));

        if (userModificationRequest.getPassword() != null &&
                !userModificationRequest.getPassword().isEmpty() &&
                !passwordEncoder.matches(userModificationRequest.getPassword(), userEntity.getPassword())) {
            userEntity.setPassword(passwordEncoder.encode(userModificationRequest.getPassword()));
        }

        if (userModificationRequest.getFirstName() != null && !
                userModificationRequest.getFirstName().isEmpty() &&
                !userModificationRequest.getFirstName().equals(userEntity.getFirstName())) {
            userEntity.setFirstName(userModificationRequest.getFirstName());
        }

        if (userModificationRequest.getSecondName() != null &&
                !userModificationRequest.getSecondName().isEmpty() &&
                !userModificationRequest.getSecondName().equals(userEntity.getSecondName())) {
            userEntity.setSecondName(userModificationRequest.getSecondName());
        }

        userRepository.save(userEntity);

        return UserModificationResponse.builder()
                .username(userEntity.getUsername())
                .modificationType(ModificationStatus.MODIFIED)
                .build();
    }
}