package com.nus.iss.sg.Mini_Project_A;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MiniProjectAApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniProjectAApplication.class, args);
	}

}
