package com.flogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class FloginApplication {

	public static void main(String[] args) {
		SpringApplication.run(FloginApplication.class, args);
	}

    @GetMapping("/hello")
    public String Hello() {
        return "hello!";
    }
}
