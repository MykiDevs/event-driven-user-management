package com.mykidevs.sharedlib.events;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;
import java.util.UUID;


@JsonDeserialize
public record UserCreatedEvent(
        UUID uuid,
        String userEmail,
        Instant createdAt,
        String token
) {
}
