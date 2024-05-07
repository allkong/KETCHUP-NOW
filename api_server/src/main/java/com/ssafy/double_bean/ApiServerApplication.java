package com.ssafy.double_bean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.Exception;
import io.sentry.Sentry;

@SpringBootApplication
public class ApiServerApplication {
	public static void main(String[] args) {
		try {
			throw new Exception("This is a test.");
		} catch (Exception e) {
			Sentry.captureException(e);
		}
	
		SpringApplication.run(ApiServerApplication.class, args);
	}
}
