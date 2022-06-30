package com.softj.itple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

//@EnableScheduling
@EnableJpaAuditing
@PropertySources({
	@PropertySource("classpath:/properties/temp.properties"),
})
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@SpringBootApplication
public class ItpleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItpleApplication.class, args);
	}

}
