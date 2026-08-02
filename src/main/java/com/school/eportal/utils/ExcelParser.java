package com.school.eportal.utils;

import com.school.eportal.data.models.Account;
import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import com.school.eportal.dtos.StudentExcelDTO;
import com.school.eportal.exceptions.InvalidDateOfBirthException;
import com.school.eportal.dtos.TeacherExcelDTO;
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
import java.util.Iterator;
import java.util.List;

@Component
public class ExcelParser {

    public List<TeacherExcelDTO> parseTeacherExcelFile(MultipartFile file) throws IOException {
        List<TeacherExcelDTO> records = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();

            if (isHeader(rows)) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                String firstName = getStringCellValue(currentRow.getCell(1));
                String lastName = getStringCellValue(currentRow.getCell(2));
                LocalDate dateOfBirth = getLocalDate(currentRow.getCell(3));
                String grade = getStringCellValue(currentRow.getCell(4));
                String division = getStringCellValue(currentRow.getCell(5));

                records.add(TeacherExcelDTO.builder()
                                .account(Account.builder()
                                        .firstName(firstName)
                                        .lastName(lastName)
                                        .birthDate(dateOfBirth)
                                        .build())
                                .grade(Grade.valueOf(grade))
                                .division(Division.valueOf(division))
                        .build());
            }
        }

        return records;
    }

    public List<StudentExcelDTO> parseStudentExcelFile(MultipartFile file) throws IOException {
        List<StudentExcelDTO> records = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(1);
            Iterator<Row> rows = sheet.iterator();


            if (isHeader(rows)) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                String firstName = getStringCellValue(currentRow.getCell(1));
                String lastName = getStringCellValue(currentRow.getCell(2));
                LocalDate dateOfBirth = getLocalDate(currentRow.getCell(3));
                String grade = getStringCellValue(currentRow.getCell(4));
                String division = getStringCellValue(currentRow.getCell(5));
                String department = getStringCellValue(currentRow.getCell(6));

                records.add(StudentExcelDTO.builder()
                                .account(Account.builder()
                                        .firstName(firstName)
                                        .lastName(lastName)
                                        .birthDate(dateOfBirth)
                                        .build())
                                .grade(Grade.valueOf(grade))
                                .division(Division.valueOf(division))
                                .department(Department.valueOf(department))
                        .build());
            }
        }

        return records;
    }

    private boolean isHeader(@NonNull Iterator<Row> rows) {
        return getStringCellValue(rows.next().getCell(0)).equals("FIRSTNAME");
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
    private LocalDate getLocalDate(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new InvalidDateOfBirthException("A Date of Birth Cell was left empty.");
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

}