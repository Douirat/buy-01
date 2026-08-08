// package com.zone01.buy01.media_service.controller;

// import com.zone01.buy01.media_service.event.*;
// import org.springframework.web.bind.annotation.*;

// import java.time.Instant;

// @RestController
// @RequestMapping("/test")
// public class KafkaTestController {

//     private final MediaEventPublisher publisher;

//     public KafkaTestController(MediaEventPublisher publisher) {
//         this.publisher = publisher;
//     }

//     @PostMapping
//     public String test() {
//         System.out.println("Sending test event to Kafka...");
//         publisher.publish(
//             new MediaUploadedEvent(
//                 "123",
//                 "seller1",
//                 "image.jpg",
//                 "image/jpeg",
//                 "PRODUCT",
//                 Instant.now()
//             )
//         );
//         return "sent for test\n";
//     }
// }