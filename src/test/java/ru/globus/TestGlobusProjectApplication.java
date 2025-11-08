package ru.globus;

import org.springframework.boot.SpringApplication;

public class TestGlobusProjectApplication {

    public static void main(String[] args) {
        SpringApplication.from(GlobusProjectApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
