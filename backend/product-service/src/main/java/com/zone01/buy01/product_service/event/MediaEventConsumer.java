package com.zone01.buy01.product_service.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MediaEventConsumer {

    @KafkaListener(
            topics = "media-events",
            groupId = "product-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(MediaUploadedEvent event) {
        System.out.println("===============MEDIAAA=================");
        System.out.println("Received MediaUploadedEvent: " + event);
        System.out.println("Media ID: " + event.mediaId());
        System.out.println("Owner ID: " + event.ownerId());
        System.out.println("Filename: " + event.filename());
        System.out.println("Content Type: " + event.contentType());
        System.out.println("Uploaded At: " + event.uploadedAt());
        System.out.println("===============MEDIAAAF=================");

}
}
