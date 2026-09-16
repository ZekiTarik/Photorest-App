package com.tarikturkdil.entity;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "create_time", updatable = false)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime createTime;
	
	@Column(name = "update_time")
    private LocalDateTime updateTime;
	
	@PrePersist
	protected void onCreate() {
		this.createTime = LocalDateTime.now();
	}
	@PreUpdate
	protected void onUpdate() {
	    this.updateTime = LocalDateTime.now();
	}
}
