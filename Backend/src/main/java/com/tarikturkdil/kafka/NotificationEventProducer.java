package com.tarikturkdil.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tarikturkdil.dto.NotificationEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationEventProducer {

	private static final String TOPIC = "notifications";
	
	@Autowired
	private KafkaTemplate<String, NotificationEvent> kafkaTemplate;
	
	public void publish(NotificationEvent event) {
		kafkaTemplate.send(TOPIC, event);
		log.info("Bildirim eventi Kafka'ya gönderildi: {}", event);
	}
}
