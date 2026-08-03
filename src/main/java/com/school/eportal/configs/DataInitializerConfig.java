package com.school.eportal.configs;


import com.school.eportal.data.models.Classroom;
import com.school.eportal.data.models.DepartmentPath;
import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import com.school.eportal.data.repositories.Classrooms;
import com.school.eportal.data.repositories.DepartmentPathRepo;
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
    private final DepartmentPathRepo departmentPathRepo;

    // Runs at the start of the project
    @Bean
    @Transactional
    public CommandLineRunner initializeDefaultEntities() {
        return args -> {
            log.info("Checking system seed entities on startup...");
            if (classrooms.count()==0) {
                createClassrooms();
            }
            if (departmentPathRepo.count()==0){
                createDepartmentPaths();
            }
            log.info("System seed entities have been successfully loaded");
        };
    }

    private void createDepartmentPaths() {
        log.info("Creating default department paths...");
        List<DepartmentPath> departmentPaths = new ArrayList<>();
        for (Department department : Department.values()) {
            if (!department.equals(Department.NONE)){
                departmentPaths.add(DepartmentPath.builder()
                        .department(department)
                        .build());
            }
        }
        departmentPathRepo.saveAll(departmentPaths);
        log.info("{} Department paths have been successfully loaded",  departmentPaths.size());
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