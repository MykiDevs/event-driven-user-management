package com.mykidevs.notiservice.repositories;

import com.mykidevs.notiservice.models.Event;
import com.mykidevs.notiservice.models.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    boolean existsByUuidAfterAndEventStatus(UUID uuid, EventStatus eventStatus);
}
