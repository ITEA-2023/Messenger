package com.itea.messenger.service.security;

import com.itea.messenger.dto.requests.UserAuthenticationAndDeletionRequest;
import com.itea.messenger.dto.requests.UserRegistrationRequest;
import com.itea.messenger.dto.responses.UserRegistrationAndAuthenticationResponse;
import com.itea.messenger.entities.token.TokenEntity;
import com.itea.messenger.entities.token.TokenType;
import com.itea.messenger.entities.user.UserEntity;
import com.itea.messenger.entities.user.UserRole;
import com.itea.messenger.repository.TokenRepository;
import com.itea.messenger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Override
    public UserRegistrationAndAuthenticationResponse register(UserRegistrationRequest userRegistrationRequest) {

        logger.info("Registration started. User to register: " + userRegistrationRequest.getUsername());

        if (userRepository.existsByUsername(userRegistrationRequest.getUsername())) {
            throw new RuntimeException("User with username: " + userRegistrationRequest.getUsername() + " already exists");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(userRegistrationRequest.getUsername())
                .password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
                .firstName(userRegistrationRequest.getFirstName())
                .secondName(userRegistrationRequest.getSecondName())
                .registrationDate(LocalDate.now())
                .role(UserRole.USER)
                .build();


        UserEntity savedUserEntity = userRepository.save(userEntity);
        logger.debug("User: " + savedUserEntity.getUsername() + " was saved to database");

        UserDetails userDetails = User.builder()
                .username(savedUserEntity.getUsername())
                .password(savedUserEntity.getPassword())
                .authorities(savedUserEntity.getRole().toString())
                .build();

        String jwt = jwtService.generateToken(userDetails);
        logger.debug("New token was generated for user: " + userDetails.getUsername());
        TokenEntity savedTokenEntity = saveUserToken(savedUserEntity, jwt);
        logger.debug("New token was saved to database");

        UserRegistrationAndAuthenticationResponse userRegistrationAndAuthenticationResponse =
                UserRegistrationAndAuthenticationResponse.builder()
                .token(savedTokenEntity.getToken())
                .build();
        logger.info("Registration was successfully finished. New user: " + userDetails.getUsername());

        return userRegistrationAndAuthenticationResponse;
    }

    @Override
    public UserRegistrationAndAuthenticationResponse authenticate(UserAuthenticationAndDeletionRequest userAuthenticationAndDeletionRequest) {

        logger.info("Authentication started. User to authenticate: " + userAuthenticationAndDeletionRequest.getUsername());

        final String username = userAuthenticationAndDeletionRequest.getUsername();
        final String password = userAuthenticationAndDeletionRequest.getPassword();

        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Invalid username")
        );

        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userAuthenticationAndDeletionRequest.getUsername(),
                        userAuthenticationAndDeletionRequest.getPassword()
                )
        );

        UserDetails userDetails = User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(userEntity.getRole().toString())
                .build();

        String jwt = jwtService.generateToken(userDetails);
        logger.debug("New token was generated for user: " + userDetails.getUsername());

        tokenRepository.removeByUserEntity(userEntity);
        logger.debug("Old token was removed from database");

        TokenEntity savedTokenEntity = saveUserToken(userEntity, jwt);
        logger.debug("New token was saved to database");

        UserRegistrationAndAuthenticationResponse userRegistrationAndAuthenticationResponse = UserRegistrationAndAuthenticationResponse.builder()
                .token(savedTokenEntity.getToken())
                .build();

        logger.info("Authentication was successfully passed. User: " + userDetails.getUsername());

        return userRegistrationAndAuthenticationResponse;
    }

    private TokenEntity saveUserToken(UserEntity userEntity, String jwt) {
        TokenEntity tokenEntity = TokenEntity.builder()
                .userEntity(userEntity)
                .token(jwt)
                .tokenType(TokenType.BEARER)
                .build();
        return tokenRepository.save(tokenEntity);
    }
}