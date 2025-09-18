package com.bolsadeideas.springboot.backend.apirest;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class SpringBootBackendApirestApplication implements CommandLineRunner{

	
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootBackendApirestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		  PasswordEncoder encoder = new BCryptPasswordEncoder(); // instancia local
	        String password = "12345";
	        for (int i = 0; i < 4; i++) {
	            System.out.println(encoder.encode(password));
	        }
		
	}

}
