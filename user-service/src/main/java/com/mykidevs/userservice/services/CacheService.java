package com.mykidevs.userservice.services;

import com.mykidevs.userservice.models.User;
import com.mykidevs.userservice.models.VerifyToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CacheService {


    @CachePut(value = "users", key = "#user.id")
    public User saveToCache(User user) {
        log.debug("saveToCache called for user id {}", user.getId());
        return user;
    }
    @CachePut(value = "veri-tokens", key = "#token.value")
    public VerifyToken saveToCache(VerifyToken token) {
        return token;
    }


    @CacheEvict(value = "users", key = "#id")
    public void removeUserFromCache(Long id) {
        return;
    }
    @CacheEvict(value = "veri-tokens", key = "#token")
    public void removeTokenFromCache(String token) {
        return;
    }




}
