package com.zikpak;


import com.zikpak.facecheck.entity.Role;
import com.zikpak.facecheck.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class FaceCheckApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceCheckApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
        return args -> {
            if(roleRepository.findByName("USER").isEmpty()){
                roleRepository.save(Role.builder().name("USER").build());
                System.out.println("Created USER role");
            }
            if(roleRepository.findByName("ADMIN").isEmpty()){
                roleRepository.save(Role.builder().name("ADMIN").build());
                System.out.println("Created ADMIN role");
            }
            if(roleRepository.findByName("FOREMAN").isEmpty()){
                roleRepository.save(Role.builder().name("FOREMAN").build());
                System.out.println("Created FOREMAN role");
            }
        };
    }
}
