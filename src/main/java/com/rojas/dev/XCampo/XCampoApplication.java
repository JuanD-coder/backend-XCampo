package com.rojas.dev.XCampo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAspectJAutoProxy
@SpringBootApplication
public class XCampoApplication {

	public static void main(String[] args) {
		SpringApplication.run(XCampoApplication.class, args);
	}

}
