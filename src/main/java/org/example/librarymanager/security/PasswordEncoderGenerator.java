package org.example.librarymanager.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String hashedPassword = encoder.encode(rawPassword);
        System.out.println("Het gehashte wachtwoord is: " + hashedPassword);
    }
}