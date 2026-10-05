package com.waiting.admission_worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AdmissionWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdmissionWorkerApplication.class, args);
	}

}
