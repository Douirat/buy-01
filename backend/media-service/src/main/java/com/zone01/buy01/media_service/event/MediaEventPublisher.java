package com.zone01.buy01.media_service.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MediaEventPublisher {

    private final KafkaTemplate<String, MediaUploadedEvent> kafkaTemplate;

    public MediaEventPublisher(
            KafkaTemplate<String, MediaUploadedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(MediaUploadedEvent event) {

        kafkaTemplate.send(
                "media-events",
                event.mediaId(),
                event);
    }
}
