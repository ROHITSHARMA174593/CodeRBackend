package com.code.codeR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;
import java.sql.Connection;

@EnableScheduling
@SpringBootApplication
public class CodeRApplication {
	public static void main(String[] args) {
		SpringApplication.run(CodeRApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(DataSource dataSource) {
		return args -> {
			try (Connection connection = dataSource.getConnection()) {
				System.out.println("✅ DATABASE CONNECTED SUCCESSFULLY!");
				System.out.println("Current Catalog: " + connection.getCatalog());
			} catch (Exception e) {
				System.err.println("❌ DATABASE CONNECTION FAILED!");
				System.err.println("Error: " + e.getMessage());
				System.err.println("TIP: If you are at college, the network might be blocking port 6543.");
				System.err.println("Try switching to port 5432 or using a mobile hotspot to test.");
			}
		};
	}
}
// todo : https://quizapplicationcoder.onrender.com
// https://quizapplicationcoder.onrender.com/swagger-ui/index.html


//todo :  git --no-pager log (with this command you can see all the commits)