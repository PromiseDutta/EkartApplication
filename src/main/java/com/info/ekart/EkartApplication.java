package com.info.ekart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;


@EnableCaching
@EnableKafka
@SpringBootApplication
@PropertySource(value = { "classpath:messages.properties" })
public class EkartApplication {

	public static void main(String[] args) {
		SpringApplication.run(EkartApplication.class, args);
	}

}