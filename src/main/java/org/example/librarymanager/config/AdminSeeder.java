// src/main/java/org/example/librarymanager/config/AdminSeeder.java

package org.example.librarymanager.config;

import org.example.librarymanager.models.Role;
import org.example.librarymanager.models.User;
import org.example.librarymanager.repositories.RoleRepository;
import org.example.librarymanager.repositories.UserRepository;
import org.springframework.boot.ApplicationRunner; // Let op de import
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class AdminSeeder {

    @Bean
    public ApplicationRunner seedAdmin(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {

                Role adminRole = roleRepository.findById("ROLE_ADMIN")
                        .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123"));

                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                adminUser.setRoles(roles);

                userRepository.save(adminUser);
                System.out.println("Default admin user created successfully.");
            }
        };
    }
}