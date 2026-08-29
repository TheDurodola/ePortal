package com.school.eportal.services;

import com.school.eportal.data.models.Account;
import com.school.eportal.data.models.Classroom;
import com.school.eportal.data.models.DepartmentPath;
import com.school.eportal.data.models.ParentChild;
import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import com.school.eportal.data.models.enums.Role;
import com.school.eportal.data.repositories.Accounts;
import com.school.eportal.data.repositories.Classrooms;
import com.school.eportal.data.repositories.DepartmentPathRepo;
import com.school.eportal.data.repositories.ParentChildRepo;
import com.school.eportal.dtos.excel.StudentExcelDTO;
import com.school.eportal.dtos.excel.StudentExcelDTOResponseBody;
import com.school.eportal.dtos.excel.TeacherExcelDTO;
import com.school.eportal.dtos.excel.TeacherExcelDTOResponseBody;
import com.school.eportal.dtos.profile.ProfileDTO;
import com.school.eportal.dtos.profile.StudentDTO;
import com.school.eportal.dtos.profile.TeacherDTO;
import com.school.eportal.dtos.responses.GetProfileResponse;
import com.school.eportal.exceptions.InvalidUserException;
import com.school.eportal.exceptions.NoSuchClassroomException;
import com.school.eportal.exceptions.UserNotFoundException;
import com.school.eportal.services.interfaces.UserService;
import com.school.eportal.utils.ExcelParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.school.eportal.utils.NameFormatter.toProperCase;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final Accounts accounts;

    private final ParentChildRepo parentChildRepo;
    private final DepartmentPathRepo departmentPathRepo;
    private final Classrooms classrooms;
    private final ExcelParser parser;

    @Override
    public GetProfileResponse getProfile(Authentication authentication) {
        ProfileDTO profile = new ProfileDTO();
        Account account = accounts.findById(Objects.requireNonNull(authentication.getPrincipal()).toString())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        processProfile(profile, account);

        if (account.getRole().equals(Role.PARENT)) {
            List<StudentDTO> children = processChildren(account);
            profile.setStudents(children);
            profile.setTeachers(processTeachers(children));
        }
        if (account.getRole().equals(Role.TEACHER)) {
            List<StudentDTO> classStudent = new ArrayList<>();
            try {
                classStudent = processClassStudents(account, classStudent);
            } catch (NoSuchClassroomException e) {
                log.info("non class teacher");
            }
            profile.setStudents(classStudent);
        }
        if (account.getRole().equals(Role.STUDENT)) {
            profile.setTeachers(setClassTeacher(account));
        }
        if (account.getRole().equals(Role.PRINCIPAL)) {
            log.info("Retrieving Principal Profile");
            profile.setStudents(getAllStudents());
            profile.setTeachers(getAllTeachers());
        } else throw new InvalidUserException("Invalid User");


        return GetProfileResponse.builder()
                .profile(profile)
                .build();
    }

    private @NonNull List<StudentDTO> getAllStudents() {
        List<Account> allStudents = accounts.findAllByRole(Role.STUDENT);
        return allStudents.stream()
                .map(student -> {
                    Department department = getDepartment(student);
                    return StudentDTO.builder()
                            .id(student.getId())
                            .firstName(toProperCase(student.getFirstName()))
                            .lastName(toProperCase(student.getLastName()))
                            .grade(classrooms.findByStudentsContaining(student.getId()).orElseThrow().getGrade())
                            .division(classrooms.findByStudentsContaining(student.getId()).orElseThrow().getDivision())
                            .department(department)
                            .build();
                })
                .toList();
    }

    private Department getDepartment(Account student) {
        if (departmentPathRepo.findByStudentsContaining(student.getId()).isPresent()) {
            return departmentPathRepo.findByStudentsContaining(student.getId()).get().getDepartment();
        }
        return Department.NONE;
    }

    private @NonNull List<TeacherDTO> getAllTeachers() {
        List<Account> allTeachers = accounts.findAllByRole(Role.TEACHER);

        return allTeachers.stream().map(teacher -> TeacherDTO.builder()
                .id(teacher.getId())
                .firstName(toProperCase(teacher.getFirstName()))
                .lastName(toProperCase(teacher.getLastName()))
                .grade(getGrade(teacher))
                .division(getDivision(teacher))
                .build()
        ).toList();
    }

    private Grade getGrade(@NonNull Account teacher) {
        if (classrooms.findByClassTeacher(teacher.getId()).isPresent()) {
            Classroom classroom = classrooms.findByClassTeacher(teacher.getId()).get();
            return classroom.getGrade();
        }
        return Grade.NONE;
    }

    private Division getDivision(@NonNull Account teacher) {
        if (classrooms.findByClassTeacher(teacher.getId()).isPresent()) {
            Classroom classroom = classrooms.findByClassTeacher(teacher.getId()).get();
            return classroom.getDivision();
        }
        return Division.NONE;
    }

    private @NonNull List<TeacherDTO> setClassTeacher(@NonNull Account account) {
        List<TeacherDTO> teachers = new ArrayList<>();
        Classroom classroom = classrooms.findByStudentsContaining(account.getId())
                .orElseThrow(() -> new NoSuchClassroomException("Student doesn't belong to a class yet."));
        Account teacher = accounts.findById(classroom.getClassTeacher())
                .orElseThrow(() -> new NoSuchClassroomException("Class doesn't have a class teacher yet."));

        teachers.add(TeacherDTO.builder()
                .firstName(toProperCase(teacher.getFirstName()))
                .lastName(toProperCase(teacher.getLastName()))
                .grade(getGrade(teacher))
                .division(getDivision(teacher))
                .build());
        return teachers;
    }

    private @NonNull List<TeacherDTO> processTeachers(@NonNull List<StudentDTO> children) {
        return children.stream()
                .map(child -> TeacherDTO.builder()
                        .id(getClassTeacherId(child))
                        .firstName(getTeacherFirstName(child))
                        .lastName(getTeacherLastName(child))
                        .grade(getGrade(child))
                        .division(getDivision(child))
                        .build())
                .toList();
    }

    private Division getDivision(StudentDTO child) {
        if (classrooms.findByStudentsContaining(child.getId()).isPresent()) {
            return classrooms.findByStudentsContaining(child.getId()).get().getDivision();
        }
        return Division.NONE;
    }

    private Grade getGrade(StudentDTO child) {
        if (classrooms.findByStudentsContaining(child.getId()).isPresent()) {
            return classrooms.findByStudentsContaining(child.getId()).get().getGrade();
        }
        return Grade.NONE;
    }

    private @NonNull List<StudentDTO> processClassStudents(Account account, List<StudentDTO> classStudent) {
        List<String> students = classrooms.findByClassTeacher(account.getId()).orElseThrow(() -> new NoSuchClassroomException("This teacher isn't a class teacher")).getStudents();
        classStudent = accounts.findAllById(students).stream()
                .map(student -> StudentDTO.builder()
                        .id(student.getId())
                        .firstName(toProperCase(student.getFirstName()))
                        .lastName(toProperCase(student.getLastName()))
                        .grade(classrooms.findByStudentsContaining(student.getId()).orElseThrow().getGrade())
                        .division(classrooms.findByStudentsContaining(student.getId()).orElseThrow().getDivision())
                        .department(getDepartment(student))
                        .build())
                .toList();
        return classStudent;
    }

    private @Nullable String getTeacherLastName(@NonNull StudentDTO child) {
        Division division = child.getDivision();
        Grade grade = child.getGrade();
        try {
            Classroom classroom = classrooms.findByGradeAndDivision(grade, division).orElseThrow(() -> new NoSuchClassroomException("This Child doesn't belong to a class"));
            Account teacher = accounts.findById(classroom.getClassTeacher()).orElseThrow(() -> new UserNotFoundException("Teacher not found"));
            return teacher.getLastName();
        } catch (NoSuchClassroomException e) {
            return null;
        }
    }

    private @Nullable String getTeacherFirstName(@NonNull StudentDTO child) {
        Division division = child.getDivision();
        Grade grade = child.getGrade();
        try {
            Classroom classroom = classrooms.findByGradeAndDivision(grade, division).orElseThrow(() -> new NoSuchClassroomException("This Child doesn't belong to a class"));
            Account teacher = accounts.findById(classroom.getClassTeacher()).orElseThrow(() -> new UserNotFoundException("Teacher not found"));
            return teacher.getFirstName();
        } catch (NoSuchClassroomException e) {
            return null;
        }
    }

    private @Nullable String getClassTeacherId(@NonNull StudentDTO child) {
        Division division = child.getDivision();
        Grade grade = child.getGrade();
        try {
            Classroom classroom = classrooms.findByGradeAndDivision(grade, division).orElseThrow(() -> new NoSuchClassroomException("This Child doesn't belong to a class"));
            Account teacher = accounts.findById(classroom.getClassTeacher()).orElseThrow(() -> new UserNotFoundException("Teacher not found"));
            return teacher.getId();
        } catch (NoSuchClassroomException e) {
            return null;
        }
    }

    private @NonNull List<StudentDTO> processChildren(@NonNull Account account) {
        List<String> childrenId = parentChildRepo.findAllByParent(account.getId()).stream()
                .map(ParentChild::getChild)
                .toList();
        return accounts.findAllById(childrenId)
                .stream()
                .map(child ->
                        StudentDTO.builder()
                                .id(child.getId())
                                .firstName(toProperCase(child.getFirstName()))
                                .lastName(toProperCase(child.getLastName()))
                                .grade(classrooms.findByStudentsContaining(child.getId()).orElseThrow().getGrade())
                                .division(classrooms.findByStudentsContaining(child.getId()).orElseThrow().getDivision())
                                .department(getDepartment(child))
                                .build())
                .toList();
    }

    private static void processProfile(@NonNull ProfileDTO profile, @NonNull Account account) {
        profile.setFirstName(toProperCase(account.getFirstName()));
        profile.setLastName(toProperCase(account.getLastName()));
        profile.setDateOfBirth(account.getDateOfBirth());
        profile.setRole(account.getRole());
        profile.setUsername(account.getUsername().toUpperCase());
    }


    private void addStudentToDepartmentPath(DepartmentPath departmentPath, Account account) {
        List<String> students = departmentPath.getStudents();
        if (students == null) {
            students = new ArrayList<>();
        }
        students.add(account.getId());
        departmentPath.setStudents(students);
    }

    private static void processList(@NonNull StudentExcelDTO student, @NonNull List<StudentExcelDTOResponseBody> response, @NonNull Account account, @NonNull Classroom classroom) {
        response.add(
                StudentExcelDTOResponseBody.builder()
                        .firstName(toProperCase(account.getFirstName()))
                        .lastName(toProperCase(account.getLastName()))
                        .schoolId(account.getUsername().toUpperCase())
                        .role(account.getRole())
                        .grade(classroom.getGrade())
                        .division(classroom.getDivision())
                        .department(student.getDepartment())
                        .build());
    }

    private static void processList(@NonNull TeacherExcelDTO teacher, @NonNull List<TeacherExcelDTOResponseBody> response, @NonNull Account account) {
        response.add(
                TeacherExcelDTOResponseBody.builder()
                        .username(account.getUsername().toUpperCase())
                        .firstName(toProperCase(account.getFirstName()))
                        .lastName(toProperCase(account.getLastName()))
                        .role(account.getRole()).grade(teacher.getGrade())
                        .division(teacher.getDivision())
                        .build()
        );
    }

    private static @NonNull Map<String, Map<String, List<String>>> prepareResponse(List<String> failCount, List<String> listOfRejectedUsernames, List<String> passCount) {
        Map<String, Map<String, List<String>>> data = new HashMap<>();
        Map<String, List<String>> passed = new HashMap<>();
        Map<String, List<String>> failed = new HashMap<>();
        failed.put("count", failCount);
        failed.put("usernames", listOfRejectedUsernames);
        passed.put("count", passCount);

        data.put("passed", passed);
        data.put("failed", failed);
        return data;
    }
}