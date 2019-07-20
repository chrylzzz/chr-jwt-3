package com.chryl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.chryl.mapper")
@SpringBootApplication
public class ChrJwt3Application {

	public static void main(String[] args) {
		SpringApplication.run(ChrJwt3Application.class, args);
	}

}
