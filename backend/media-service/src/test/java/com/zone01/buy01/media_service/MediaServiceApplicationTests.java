package com.zone01.buy01.media_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.zone01.buy01.media_service.event.MediaUploadedEvent;

@SpringBootTest
class MediaServiceApplicationTests {

	@MockitoBean
	private KafkaTemplate<String, MediaUploadedEvent> kafkaTemplate;
	@Test
	void contextLoads() {
	}

}
