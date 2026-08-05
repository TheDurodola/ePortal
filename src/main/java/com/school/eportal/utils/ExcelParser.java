package com.school.eportal.utils;

import com.school.eportal.data.models.Account;
import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import com.school.eportal.dtos.excel.SchoolFeeExcelExtractDTO;
import com.school.eportal.dtos.excel.StudentExcelDTO;
import com.school.eportal.dtos.excel.TeacherExcelDTO;
import com.school.eportal.exceptions.EmptyCellException;
import com.school.eportal.exceptions.ExcelParserException;
import com.school.eportal.exceptions.InvalidDateOfBirthException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component

public class ExcelParser {

    public List<TeacherExcelDTO> parseTeacherExcelFile(MultipartFile file) throws IOException {
        log.info("Parsing Teacher Sheet");
        List<TeacherExcelDTO> records = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row currentRow : sheet) {
                if (isHeader(currentRow)) {
                    continue;
                }
                String firstName = getStringCellValue(currentRow.getCell(0));
                String lastName = getStringCellValue(currentRow.getCell(1));

                LocalDate dateOfBirth;
                try {
                    dateOfBirth = getLocalDate(currentRow.getCell(2));
                } catch (EmptyCellException e) {
                    log.info("Teacher {} {} has an invalid date of birth ", firstName, lastName);
                    continue;
                }
                String grade = getStringCellValue(currentRow.getCell(3));
                String division = getStringCellValue(currentRow.getCell(4));

                if (isLastRow(firstName, lastName)) {
                    break;
                }

                Grade gradeValue;
                Division divisionValue;
                try {
                    gradeValue = Grade.valueOf(grade.toUpperCase());
                    divisionValue = Division.valueOf(division.toUpperCase());
                } catch (IllegalArgumentException e) {
                    gradeValue = Grade.NONE;
                    divisionValue = Division.NONE;
                }

                records.add(TeacherExcelDTO.builder()
                        .account(Account.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .dateOfBirth(dateOfBirth)
                                .build())
                        .grade(gradeValue)
                        .division(divisionValue)
                        .build());
            }
        }

        return records;
    }

    public List<StudentExcelDTO> parseStudentExcelFile(MultipartFile file) throws IOException {
        log.info("Parsing Student Sheet");
        List<StudentExcelDTO> records = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(1);

            for (Row currentRow : sheet) {
                if (isHeader(currentRow)){
                    continue;
                }
                String firstName = getStringCellValue(currentRow.getCell(0));
                String lastName = getStringCellValue(currentRow.getCell(1));
                LocalDate dateOfBirth;
                try {
                    dateOfBirth = getLocalDate(currentRow.getCell(2));
                } catch (EmptyCellException e) {
                    continue;
                }
                String  grade = getStringCellValue(currentRow.getCell(3));
                String division = getStringCellValue(currentRow.getCell(4));
                String department = getStringCellValue(currentRow.getCell(5));

                Department value;
                try {
                    value = Department.valueOf(department);
                } catch (IllegalArgumentException e) {
                    value = Department.NONE;
                }
                records.add(StudentExcelDTO.builder()
                        .account(Account.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .dateOfBirth(dateOfBirth)
                                .build())
                        .grade(Grade.valueOf(grade))
                        .division(Division.valueOf(division))
                        .department(value)
                        .build());
            }
        }

        return records;
    }


    public List<SchoolFeeExcelExtractDTO> parseSchoolFeeExcelFile(MultipartFile file) throws IOException {
        log.info("Parsing School Fees Sheet");

        List<SchoolFeeExcelExtractDTO> records = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row currentRow : sheet) {
                if (isHeader(currentRow)){
                    continue;
                }
                String session = getStringCellValue(currentRow.getCell(0));
                String departmentInString = getStringCellValue(currentRow.getCell(1));
                String grade = getStringCellValue(currentRow.getCell(2));
                long tuition = getLongCellValue(currentRow.getCell(3));

                Department department;
                if (departmentInString == null || departmentInString.isBlank()) {
                    department = Department.NONE;
                }else department = Department.valueOf(departmentInString);

                records.add(SchoolFeeExcelExtractDTO
                        .builder()
                                .schoolName(session)
                                .tuition(tuition)
                                .department(department)
                                .grade(Grade.valueOf(grade.toUpperCase()))
                        .build());
            }
        }

        return records;
    }


    private boolean isHeader(@NonNull Row currentRow) {
        String firstCell = getStringCellValue(currentRow.getCell(0));
        String secondCell = getStringCellValue(currentRow.getCell(1));
        String thirdCell = getStringCellValue(currentRow.getCell(2));
        List<String> list = new ArrayList<>();
        list.add(firstCell);
        list.add(secondCell);
        list.add(thirdCell);

        return list.contains("FIRSTNAME") || list.contains("DATE OF BIRTH") || list.contains("LASTNAME");

    }

    private static boolean isLastRow(String firstName, String lastName) {
        return (firstName == null || firstName.isEmpty()) && (lastName == null || lastName.isEmpty());
    }

    private String getStringCellValue(Cell cell)  {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
    private LocalDate getLocalDate(Cell cell) throws EmptyCellException {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new EmptyCellException("A Date of Birth Cell was left empty.");
        }

        // Case 1: Properly formatted Excel Date cell
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            LocalDateTime dateTime  = cell.getLocalDateTimeCellValue();
            if (dateTime == null) {
                throw new InvalidDateOfBirthException("Date of Birth cell contains an invalid numeric date.");
            }
            return dateTime.toLocalDate();
        }

        // Case 2: Date entered as Text (e.g., "2023-10-15" or "15/10/2023")
        if (cell.getCellType() == CellType.STRING) {
            String textValue = cell.getStringCellValue().trim();
            if (textValue.isEmpty()) {
                throw new InvalidDateOfBirthException("A Date of Birth Cell was left empty.");
            }
            try {
                // Adjust the format pattern to match expected incoming string formats
                return LocalDate.parse(textValue, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                throw new InvalidDateOfBirthException("Invalid date text format: " + textValue);
            }
        }

        throw new InvalidDateOfBirthException("Unsupported cell format for Date of Birth.");
    }

    private Long getLongCellValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> (long) cell.getNumericCellValue();
            case STRING -> parseStringToLong(cell.getStringCellValue().trim());
            case BLANK -> null;
            default -> throw new ExcelParserException(
                    "Unsupported cell type for numeric value: " + cell.getCellType()
                            + " at row " + cell.getRowIndex() + ", column " + cell.getColumnIndex());
        };
    }

    private Long parseStringToLong(String value) {
        if (value.isEmpty()) return null;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ExcelParserException(
                    "Expected a numeric value but found '" + value + "'");
        }
    }

}