package com.example.demo;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@SpringBootApplication
@RestController
public class DemoApplication {

	public static void main(String[] args)
	{
		SpringApplication application = new SpringApplication(DemoApplication.class);
		application.setBannerMode(Banner.Mode.OFF);
		application.run(args);
//		SpringApplication.run(DemoApplication.class, args);
	}

//	@GetMapping("/hello")
//	public String helloworld() {
//		return "helloworld";
//	}
}
