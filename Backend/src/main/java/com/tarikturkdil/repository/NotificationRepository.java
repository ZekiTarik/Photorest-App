package com.tarikturkdil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarikturkdil.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreateTimeDesc(Long recipientId);

    long countByRecipientIdAndIsReadFalse(Long recipientId);
}