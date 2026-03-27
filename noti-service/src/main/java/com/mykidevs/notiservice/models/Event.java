package com.mykidevs.notiservice.models;


import com.mykidevs.notiservice.models.enums.EventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt = Instant.now();
    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false)
    private EventStatus eventStatus;
}
