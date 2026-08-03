package com.school.eportal.services;

import com.school.eportal.data.models.Account;
import com.school.eportal.data.models.Classroom;
import com.school.eportal.data.models.DepartmentPath;
import com.school.eportal.data.models.ParentChild;
import com.school.eportal.data.models.enums.AccountStatus;
import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Grade;
import com.school.eportal.data.models.enums.Role;
import com.school.eportal.data.repositories.Accounts;
import com.school.eportal.data.repositories.Classrooms;
import com.school.eportal.data.repositories.DepartmentPathRepo;
import com.school.eportal.data.repositories.ParentChildRepo;
import com.school.eportal.dtos.*;
import com.school.eportal.dtos.requests.AccountActivationRequest;
import com.school.eportal.dtos.requests.ParentRegistrationRequest;
import com.school.eportal.dtos.requests.RegisterBulkUsersRequest;
import com.school.eportal.dtos.responses.AccountActivationResponse;
import com.school.eportal.dtos.responses.ParentRegistrationResponse;
import com.school.eportal.dtos.responses.PreRegistrationResponse;
import com.school.eportal.dtos.responses.RegisterBulkUsersResponse;
import com.school.eportal.exceptions.*;
import com.school.eportal.security.dtos.responses.AccountResponse;
import com.school.eportal.services.interfaces.AccountService;
import com.school.eportal.utils.ExcelParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.school.eportal.utils.Mutator.mutate;
import static com.school.eportal.utils.NameFormatter.toProperCase;
import static com.school.eportal.utils.RandomPicker.generateSixRandomNumber;
import static com.school.eportal.utils.Validator.validateExcelFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final Accounts accounts;
    private final ParentChildRepo parentChildRepo;
    private final DepartmentPathRepo departmentPathRepo;
    private final Classrooms classrooms;
    private final PasswordEncoder passwordEncoder;
    private final ExcelParser parser;

    @Override
    public AccountResponse getUserAccountBy(String username) throws AccountNotFoundException {
        Account account = accounts.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .password(account.getPassword())
                .role(account.getRole())
                .accountStatus(account.getStatus())
                .build();
    }

    @Override
    public AccountResponse getUserAccountById(String id) throws AccountNotFoundException {
        Account account = accounts.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .password(account.getPassword())
                .role(account.getRole())
                .accountStatus(account.getStatus())
                .build();
    }

    @Override
    @Transactional
    public RegisterBulkUsersResponse bulkRegistration(@NonNull RegisterBulkUsersRequest request) {
        List<String> failCount = new ArrayList<>();
        List<String> listOfRejectedUsernames = new ArrayList<>();
        List<String> passCount = new ArrayList<>();

        List<String> usernames = request.getData().stream()
                .map(BulkAccountDTO::getUsername)
                .toList();

        List<Account> existingUsers = accounts.findAllByUsernameIn(usernames);

        if (!existingUsers.isEmpty()) {
            failCount.add(String.valueOf(existingUsers.size()));
            listOfRejectedUsernames = existingUsers.stream()
                    .map(Account::getUsername)
                    .toList();
        }

        List<String> finalListOfRejectedUsernames = listOfRejectedUsernames;
        List<Account> list = request.getData().stream()
                .filter(account -> !finalListOfRejectedUsernames.contains(account.getUsername()))
                .map(bulkAccountDTO -> Account.builder()
                        .firstName(bulkAccountDTO.getFirstName())
                        .username(bulkAccountDTO.getUsername())
                        .lastName(bulkAccountDTO.getLastName())
                        .status(AccountStatus.INACTIVE)
                        .dateOfBirth(bulkAccountDTO.getDateOfBirth())
                        .role(bulkAccountDTO.getRole())
                        .build())
                .toList();

        if (list.isEmpty()) {
            throw new InvalidBulkRegistration("All usernames are already taken.");
        }
        List<Account> accounts1 = accounts.saveAll(list);

        passCount.add(String.valueOf(accounts1.size()));



        return RegisterBulkUsersResponse.builder()
                .data(prepareResponse(failCount, listOfRejectedUsernames, passCount))
                .build();

    }

    @Override
    public PreRegistrationResponse preRegistration(@NonNull MultipartFile file) {
        List<StudentExcelDTOResponseBody> students;
        List<TeacherExcelDTOResponseBody> teachers;
        try {
            validateExcelFile(file);
        } catch (IOException e) {
            throw new ValidatorException(e);
        }
        if (!Objects.equals(file.getOriginalFilename(), "PreRegistration")) {
            throw new InvalidPreRegistrationException("Incorrect file name");
        }
        try {
             students = processStudents(file);
             teachers = processTeachers(file);
        } catch (IOException e) {
            throw new ExcelParserException(e);
        }

        if (students.isEmpty() && teachers.isEmpty()) {
            throw new InvalidPreRegistrationException("All students and teachers sheets are empty.");
        }
        return PreRegistrationResponse.builder()
                .students(students)
                .teachers(teachers)
                .build();
    }

    private @NonNull List<TeacherExcelDTOResponseBody> processTeachers(@NonNull MultipartFile file) throws IOException {
        List<TeacherExcelDTO> teachers = parser.parseTeacherExcelFile(file);
        List<TeacherExcelDTOResponseBody> response = new ArrayList<>();
        teachers.forEach(teacher -> {
            Account account = teacher.getAccount();
            account.setRole(Role.TEACHER);
            account.setStatus(AccountStatus.INACTIVE);
            account.setUsername("tr" + generateSixRandomNumber());
            accounts.save(account);
            if (!teacher.getGrade().equals(Grade.NONE)) {
                Classroom classroom = classrooms.findByGradeAndDivision(teacher.getGrade(), teacher.getDivision())
                        .orElseThrow(() -> new InvalidClassroomException("Invalid Grade/Division for "
                                + account.getFirstName() + " " + account.getLastName()));
                classroom.setClassTeacher(account);

                classrooms.save(classroom);
            }
            processList(teacher, response, account);
        });
        return response;
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

    private @NonNull List<StudentExcelDTOResponseBody> processStudents(@NonNull MultipartFile file) throws IOException {
        List<StudentExcelDTO> students = parser.parseStudentExcelFile(file);
        List<StudentExcelDTOResponseBody> response = new ArrayList<>();
        students.forEach(student -> {
            Account account = student.getAccount();
            account.setStatus(AccountStatus.INACTIVE);
            account.setRole(Role.STUDENT);
            account.setUsername("st" + generateSixRandomNumber());
            Account savedStudent = accounts.save(account);
            Classroom classroom = classrooms.findByGradeAndDivision(student.getGrade()
                            , student.getDivision())
                    .orElseThrow(() -> new InvalidClassroomException("Invalid Grade/Division for "
                            + account.getFirstName() + " " + account.getLastName()));
            classroom.getStudents().add(savedStudent);
            classrooms.save(classroom);
            if (!student.getDepartment().equals(Department.NONE)) {
                DepartmentPath departmentPath = departmentPathRepo.findFirstByDepartment(student.getDepartment())
                        .orElseThrow(() -> new DepartmentPathException("Critical: Department Path not found"));

                departmentPath.addStudent(savedStudent);
                departmentPathRepo.save(departmentPath);
            }
            processList(student, response, account, classroom);
        });
        return response;
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

    @Override
    public ParentRegistrationResponse parentRegistration(ParentRegistrationRequest request){
        mutate(request);
        if (accounts.existsByUsername(request.getUsername())){
            throw new InvalidUsernameException("Email already taken");
        }

        Account childAccount = accounts.findByUsername(request.getChildSchoolId())
                .orElseThrow(() -> new InvalidUsernameException("Invalid Child School ID"));

        if (!childAccount.getDateOfBirth().equals(request.getChildDateOfBirth())) {
            throw new InvalidDateOfBirthException("Incorrect Child DatePfBirth");
        }

        Account newParentAccount = Account.builder()
                .firstName(request.getFirstName())
                .role(Role.PARENT)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .lastName(request.getLastName())
                .build();

        Account savedParentAccount = accounts.save(newParentAccount);

        ParentChild relationship = ParentChild.builder()
                .parent(savedParentAccount)
                .child(childAccount)
                .build();
        parentChildRepo.save(relationship);


            return ParentRegistrationResponse.builder()
                    .parentFirstName(toProperCase(newParentAccount.getFirstName()))
                    .childFirstName(toProperCase(childAccount.getFirstName()))
                    .build();
    }

    @Override
    public AccountActivationResponse accountActivation(@NonNull AccountActivationRequest request) {
        Account savedAccount = accounts.findByUsername(request.getUsername().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (savedAccount.getStatus().equals(AccountStatus.ACTIVE) || savedAccount.getPassword().isBlank()) {
            throw new InvalidAccountStatusException("Account is already active.");
        }
        if (!savedAccount.getDateOfBirth().equals(request.getDateOfBirth())){
            throw new InvalidDateOfBirthException("Invalid Date of Birth");
        }

        savedAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        savedAccount.setStatus(AccountStatus.ACTIVE);

        accounts.save(savedAccount);

        return AccountActivationResponse.builder()
                .firstName(toProperCase(savedAccount.getFirstName()))
                .build();
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
