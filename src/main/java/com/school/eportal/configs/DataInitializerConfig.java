package com.school.eportal.configs;


import com.school.eportal.data.models.Classroom;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import com.school.eportal.data.repositories.Classrooms;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializerConfig {

    private final Classrooms classrooms;

    // Runs at the start of the project
    @Bean
    @Transactional
    public CommandLineRunner initializeDefaultEntities() {
        return args -> {
            log.info("Checking system seed entities on startup...");
            if (classrooms.count()==0) {
                createClassrooms();
            }
            log.info("System seed entities have been successfully loaded");
        };
    }

    // Creates Classrooms at the start of the project
    private void createClassrooms() {
        log.info("Creating default classrooms...");
        List<Classroom> newClassrooms = new ArrayList<>();

        for (Grade grade : Grade.values()) {
            for (Division division : Division.values()) {
                if (division != null && !Division.NONE.equals(division)
                        && grade != null && !Grade.NONE.equals(grade)) {

                    newClassrooms.add(Classroom.builder()
                            .division(division)
                            .grade(grade)
                            .build());
                }
            }
        }
        classrooms.saveAll(newClassrooms);
        log.info("A total of {} classrooms have been created", newClassrooms.size());
    }
}