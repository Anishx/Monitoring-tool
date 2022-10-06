package com.monitoring.Springbackend;

import com.monitoring.Springbackend.model.MonitorModel;
import com.monitoring.Springbackend.repository.MonitorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SpringBootApplication
public class SpringBackendApplication {

	@PostConstruct
	public void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(MonitorRepository monitorRepository){
		return args -> {
			MonitorModel model = new MonitorModel("Google","www.google.com",5l );
			monitorRepository.save(model);
		};
	}

	@Bean
	public ScheduledExecutorService scheduler(){
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);
		return scheduledExecutorService;
	}


}
