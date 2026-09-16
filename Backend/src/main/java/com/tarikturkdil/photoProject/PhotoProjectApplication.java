package com.tarikturkdil.photoProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@ComponentScan(basePackages = {"com.tarikturkdil"})
@EntityScan(basePackages = {"com.tarikturkdil"})
@EnableJpaRepositories(basePackages = {"com.tarikturkdil"})
@EnableKafka
@SpringBootApplication
public class PhotoProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoProjectApplication.class, args);
	}

}
