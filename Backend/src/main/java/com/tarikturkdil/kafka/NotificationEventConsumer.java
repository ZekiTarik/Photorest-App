package com.tarikturkdil.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tarikturkdil.dto.NotificationEvent;
import com.tarikturkdil.entity.Notification;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.repository.NotificationRepository;
import com.tarikturkdil.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationEventConsumer {

	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@KafkaListener(topics = "notifications", groupId = "photoproject-notification-group")
	public void consume(NotificationEvent event) {
		log.info("Bildirim eventi alındı: {}", event);
		
		User recipient = userRepository.findById(event.getRecipientId())
				.orElse(null);
		
		User actor = userRepository.findById(event.getActorId())
				.orElse(null);
		
		
		if (recipient == null || actor == null) {
            log.warn("Bildirim oluşturulamadı: kullanıcı bulunamadı. event={}", event);
            return;
        }
		
		Notification notification = new Notification();
		notification.setRecipient(recipient);
		notification.setActor(actor);
		notification.setType(event.getType());
		notification.setReferenceId(event.getReferenceId());
		
		notificationRepository.save(notification);
		log.info("Bildirim veritabanına kaydedildi: recipient={}, type={}", recipient.getEmail(), event.getType());
	}
}
