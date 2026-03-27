package com.mykidevs.notiservice.listeners;

import com.mykidevs.notiservice.models.Event;
import com.mykidevs.notiservice.models.enums.EventStatus;
import com.mykidevs.notiservice.repositories.EventRepository;
import com.mykidevs.notiservice.services.MailService;
import com.mykidevs.sharedlib.events.UserCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@AllArgsConstructor
@Slf4j
@Component
public class EventListener {
    private final EventRepository eventRepository;
    private final MailService mailService;
    @Transactional
    @KafkaListener(topics = "user-creation-topic", groupId = "noti-group")
    public void onUserCreated(UserCreatedEvent event) {
            if(eventRepository.existsByUuidAfterAndEventStatus(event.uuid(), EventStatus.SENT)) {
                log.warn("Duplicate event skipped, uuid={}", event.uuid());
                return;
            }
            Event eventEntity = new Event();
            eventEntity.setUuid(event.uuid());
            eventEntity.setEventStatus(EventStatus.PROCESSING);
            eventEntity.setUserEmail(event.userEmail());
            eventEntity.setOccurredAt(Instant.now());
            mailService.sendVerifyMail(event.token(), event.userEmail());
            eventEntity.setEventStatus(EventStatus.SENT);
            eventRepository.save(eventEntity);
    }

}
