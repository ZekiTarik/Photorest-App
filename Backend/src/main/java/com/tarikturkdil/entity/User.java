package com.tarikturkdil.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails{

	@Column(name = "name",unique = true, nullable = false)
	private String name;
	
	@Column(name = "email",unique = true, nullable = false)
	private String email;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	@Column(name = "avatarUrl") //Cloudinary'den gelecek profil resmi linki
	private String avatarUrl;
	
	@Column(name = "bio")
	private String bio;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return List.of();
	}
	
	@Override
    public String getUsername() {
        return this.email;
    }
}
