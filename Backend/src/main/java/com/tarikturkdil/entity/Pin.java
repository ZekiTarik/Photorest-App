package com.tarikturkdil.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pin extends BaseEntity {

	@Column(name = "title",nullable = false)
	private String title;
	
	@Column(name = "description",length = 500)
	private String description;
	
	@Column(name = "image_url", nullable = false)
	private String imageUrl;
	
	@Column(name = "image_public_id", nullable = false)
	private String imagePublicId;
	
	// Pin.java
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}
