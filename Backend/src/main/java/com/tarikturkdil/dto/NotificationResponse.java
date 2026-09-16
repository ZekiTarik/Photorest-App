package com.tarikturkdil.dto;

import com.tarikturkdil.entity.NotificationType;

import lombok.Data;

@Data
public class NotificationResponse extends DtoBase{

	private String actorName;
	private String actorAvatarUrl;
	private NotificationType type;
	private Long referenceId;
	private boolean isRead;
}
