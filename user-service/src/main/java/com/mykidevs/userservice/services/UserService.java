package com.mykidevs.userservice.services;


import com.mykidevs.sharedlib.events.UserCreatedEvent;
import com.mykidevs.userservice.dto.requests.UserCreateRequest;
import com.mykidevs.userservice.dto.requests.UserPagePaginationRequest;
import com.mykidevs.userservice.dto.requests.UserUpdateRequest;
import com.mykidevs.userservice.dto.responses.UserResponse;
import com.mykidevs.userservice.exceptions.InvalidSortPropertyException;
import com.mykidevs.userservice.exceptions.UserAlreadyExistsException;
import com.mykidevs.userservice.exceptions.UserNotFoundException;
import com.mykidevs.userservice.mappers.UserMapper;
import com.mykidevs.userservice.models.User;
import com.mykidevs.userservice.models.VerifyToken;
import com.mykidevs.userservice.repositories.UserRepository;
import com.mykidevs.userservice.repositories.VerifyTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final CacheService cacheService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final VerifyTokenRepository verifyTokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
      "id", "email", "description", "hasVerifiedEmail"
    );
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean verifyToken(String token) {
        Optional<Long> optUserId = verifyTokenRepository.findUserIdByToken(token);
        if(optUserId.isPresent()) {
            verifyTokenRepository.deleteByValueAndUserId(token, optUserId.get());
            cacheService.removeTokenFromCache(token);
            userRepository.activateUser(optUserId.get());
        }
        return optUserId.isPresent();
    }

    public Page<UserResponse> getAllUsersWithPagePagination(UserPagePaginationRequest request) {
        if (!ALLOWED_SORT_FIELDS.contains(request.sortBy())) {
            throw new InvalidSortPropertyException("Invalid property: " + request.sortBy());
        }
        Pageable pageable = PageRequest.of(request.page(), request.size(),
                Sort.by(Sort.Direction.fromString(request.direction()), request.sortBy()));
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toDto);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    private User getUserByEmail(String email) {
        return userRepository.getByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest dto) {
        User userToUpdate = getUserById(id);
        userMapper.updateUserFromRequest(dto, userToUpdate);
        User saved = userRepository.save(userToUpdate);
        cacheService.saveToCache(saved);
        return userMapper.toDto(saved);
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserResponse get(Long id) {
        return userMapper.toDto(getUserById(id));
    }


    @Transactional
    public UserResponse create(UserCreateRequest dto) {
        if(userRepository.existsByEmail(dto.email())) {
            throw new UserAlreadyExistsException("Nah, user already exists");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);


        var data = UUID.randomUUID().toString().replace("-", "");
        var verToken = VerifyToken.builder()
                .user(user)
                .value(data)
                .expDate(Instant.now().plusSeconds(60 * 60 * 1)).build();
        var event = new UserCreatedEvent(
                UUID.randomUUID(),
                dto.email(),
                Instant.now(),
                data
                );
        verifyTokenRepository.save(verToken);
        eventPublisher.publishEvent(event);
        cacheService.saveToCache(user);
        cacheService.saveToCache(verToken);
        return userMapper.toDto(user);
    }


    @Transactional
    public void delete(Long id) {
        cacheService.removeUserFromCache(id);
        userRepository.deleteById(id);
    }

    @Async // it is better to use outbox pattern with CDC app (debezium)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEvent(UserCreatedEvent event) {
        kafkaTemplate.send("user-creation-topic", event.userEmail(), event)
                .whenComplete((res, ex) -> {
                    if(ex == null) {
                        log.info("Event was sent with UUID: {}", event.uuid());
                    } else {
                        log.error("Failed to sent event", ex);
                    }
                });
    }
}
