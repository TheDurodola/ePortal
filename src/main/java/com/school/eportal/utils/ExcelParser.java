package com.school.eportal.utils;

import com.school.eportal.data.models.Account;
import com.school.eportal.data.models.enums.Department;
import com.school.eportal.data.models.enums.Division;
import com.school.eportal.data.models.enums.Grade;
import com.school.eportal.dtos.excel.SchoolFeeExcelExtractDTO;
import com.school.eportal.dtos.excel.StudentExcelDTO;
import com.school.eportal.dtos.excel.TeacherExcelDTO;
import com.school.eportal.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

            int lastRowNum = sheet.getLastRowNum();
            if (isRegistrationHeader(sheet.getRow(sheet.getFirstRowNum()))){
                sheet.removeRow(sheet.getRow(sheet.getFirstRowNum()));
            }

            for (Row currentRow : sheet) {

                String firstName;
                try {
                    firstName = getStringCellValue(currentRow.getCell(0));
                }catch (InvalidCellValueException e){
                    break;
                }
                String lastName = getStringCellValue(currentRow.getCell(1));

                LocalDate dateOfBirth;
                try {
                    dateOfBirth = getLocalDate(currentRow.getCell(2));
                } catch (EmptyCellException e) {
                    log.info("Teacher {} {} has an invalid date of birth ", firstName, lastName);
                    continue;
                }
                String grade;
                String division;
                try {
                    grade = getStringCellValue(currentRow.getCell(3));
                     division = getStringCellValue(currentRow.getCell(4));
                } catch (InvalidCellValueException e) {
                    grade = "NONE";
                    division = "NONE";
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
                if (isLastRow(lastRowNum, currentRow)) {
                    break;
                }
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

            int lastRowNum = sheet.getLastRowNum();

            if (isRegistrationHeader(sheet.getRow(sheet.getFirstRowNum()))){
                sheet.removeRow(sheet.getRow(sheet.getFirstRowNum()));
            }

            for (Row currentRow : sheet) {

                String firstName;
                try {
                    firstName = getStringCellValue(currentRow.getCell(0));
                }catch (InvalidCellValueException e){
                    break;
                }
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

                if (isLastRow(lastRowNum, currentRow)) {
                    break;
                }
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
            String session = sheet.getSheetName();

            int lastRowNum = sheet.getLastRowNum();
            for (Row currentRow : sheet) {
                if (isSchoolFeeHeader(currentRow)){
                    continue;
                }
                String departmentInString = getStringCellValue(currentRow.getCell(0));
                String grade = getStringCellValue(currentRow.getCell(1));
                BigDecimal tuition = getBigDecimalCellValue(currentRow.getCell(2));
                BigDecimal firstTermMinPercentage = getBigDecimalCellValue(currentRow.getCell(3));
                BigDecimal secondTermMinPercentage = getBigDecimalCellValue(currentRow.getCell(4));
//                BigDecimal thirdTermMinPercentage = getBigDecimalCellValue(currentRow.getCell(5));

                if (Validator.isValidSessionFormat(session)) {
                    throw new ExcelParserException(identifyCell(currentRow.getCell(0)) + " Invalid session format." +
                            " Kindly use 2019/20 or 2019/2020");
                }
                Department department;
                if (departmentInString == null || departmentInString.isBlank()) {
                    department = Department.NONE;
                }else department = Department.valueOf(departmentInString);
                validatePercentages(firstTermMinPercentage, secondTermMinPercentage, currentRow);
                records.add(SchoolFeeExcelExtractDTO
                        .builder()
                            .session(session)
                            .tuition(tuition)
                            .department(department)
                            .firstTermMinPercentage(firstTermMinPercentage
                                    .round(new  MathContext(2, RoundingMode.HALF_UP)))
                            .secondTermMinPercentage(secondTermMinPercentage
                                    .round(new  MathContext(2, RoundingMode.HALF_UP)))
                            .thirdTermMinPercentage(BigDecimal.valueOf(100)
                                    .round(new  MathContext(2, RoundingMode.HALF_UP)))
                            .grade(Grade.valueOf(grade.toUpperCase()))
                        .build());

                if (isLastRow(lastRowNum, currentRow)) {
                    break;
                }
            }
        }

        return records;
    }

    private void validatePercentages(BigDecimal firstTermMinPercentage, BigDecimal secondTermMinPercentage, Row row) {
        validateIfPercentagesAreWithinZeroAndOneHundred(firstTermMinPercentage, secondTermMinPercentage);
        if (!(firstTermMinPercentage.compareTo(secondTermMinPercentage) <= 0)) {
            throw new InvalidPercentageException("Row:" + row.getRowNum() + "First term minimum percentage can't be greater than the Second term minimum percentage");
        }

        if (!(secondTermMinPercentage.compareTo(BigDecimal.valueOf(100)) <= 0)) {
            throw new InvalidPercentageException("Row:" + row.getRowNum() + "Second term minimum percentage can't be greater than the Third term minimum percentage");
        }

    }

    private static void validateIfPercentagesAreWithinZeroAndOneHundred(BigDecimal firstTermMinPercentage, BigDecimal secondTermMinPercentage) {
        if (isGreaterThanOneHundredPercentage(firstTermMinPercentage, secondTermMinPercentage)) {
            throw new InvalidPercentageException("Percentage can't be greater than 100%");
        }

        if (isLessThanZeroPercentage(firstTermMinPercentage, secondTermMinPercentage)) {
            throw new InvalidPercentageException("Percentage can't be greater than 100%");
        }
    }

    private static boolean isGreaterThanOneHundredPercentage(@NonNull BigDecimal firstTermMinPercentage, BigDecimal secondTermMinPercentage) {
        return firstTermMinPercentage.compareTo(BigDecimal.valueOf(100)) > 0 || secondTermMinPercentage.compareTo(BigDecimal.valueOf(100)) > 0;
    }

    private static boolean isLessThanZeroPercentage(@NonNull BigDecimal firstTermMinPercentage, BigDecimal secondTermMinPercentage) {
        return firstTermMinPercentage.compareTo(BigDecimal.ZERO) < 0 || secondTermMinPercentage.compareTo(BigDecimal.ZERO) < 0 ;
    }

    private boolean isRegistrationHeader(@NonNull Row currentRow) {
        String firstCell = getStringCellValue(currentRow.getCell(0));
        String secondCell = getStringCellValue(currentRow.getCell(1));
        String thirdCell = getStringCellValue(currentRow.getCell(2));
        List<String> list = new ArrayList<>();
        list.add(firstCell);
        list.add(secondCell);
        list.add(thirdCell);

        return list.contains("S/N") || list.contains("DEPARTMENT") ||
                list.contains("GRADE") || list.contains("TUITION") || list.contains("FIRSTNAME")
                || list.contains("LASTNAME");

    }

    private boolean isSchoolFeeHeader(@NonNull Row currentRow) {
        String firstCell = getStringCellValue(currentRow.getCell(0));
        String secondCell = getStringCellValue(currentRow.getCell(1));
        String thirdCell = getStringCellValue(currentRow.getCell(2));
        String fourthCell = getStringCellValue(currentRow.getCell(3));

        List<String> list = new ArrayList<>();
        list.add(firstCell);
        list.add(secondCell);
        list.add(thirdCell);
        list.add(fourthCell);

        return list.contains("SESSION") || list.contains("DEPARTMENT") || list.contains("GRADE")
                || list.contains("TUITION");

    }

    private static boolean isLastRow(int lastRowNumber, @NonNull Row currentCell) {
        return currentCell.getRowNum() == lastRowNumber;
    }

    private String getStringCellValue(Cell cell)  {
        if (cell == null) throw new  InvalidCellValueException(" A cell in the spreadsheet is null.");
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> throw new InvalidCellValueException(identifyCell(cell)  + " is having an invalid format.");
        };
    }

    private BigDecimal getBigDecimalCellValue(Cell cell) {

        if (cell == null) throw new InvalidCellValueException("A cell is null;");
        return switch (cell.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> {
                String raw = cell.getStringCellValue().trim();
                if (raw.isEmpty()) throw new InvalidCellValueException(identifyCell(cell)  +" cell is null;");
                try {
                    yield new BigDecimal(raw);
                } catch (NumberFormatException e) {
                    throw new InvalidCellValueException(identifyCell(cell) + " is having an invalid format.");
                }
            }
            case FORMULA -> BigDecimal.valueOf(cell.getNumericCellValue()); // only safe if evaluator already ran
            default -> throw new InvalidCellValueException(identifyCell(cell)  + " is having an invalid format.");
        };
    }

    private static @NonNull String identifyCell(@NonNull Cell cell) {
        return "Sheet:" + cell.getSheet().getSheetName() + " Cell:" +
                cell.getAddress().toString();
    }


    private LocalDate getLocalDate(Cell cell) throws EmptyCellException {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            throw new EmptyCellException("A Date of Birth cell null.");
        }

        // Case 1: Properly formatted Excel Date cell
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            LocalDateTime dateTime  = cell.getLocalDateTimeCellValue();
            if (dateTime == null) {
                throw new InvalidDateOfBirthException(identifyCell(cell) + " cell contains an invalid numeric date.");
            }
            return dateTime.toLocalDate();
        }

        // Case 2: Date entered as Text (e.g., "2023-10-15" or "15/10/2023")
        if (cell.getCellType() == CellType.STRING) {
            String textValue = cell.getStringCellValue().trim();
            if (textValue.isEmpty()) {
                throw new InvalidDateOfBirthException(identifyCell(cell) + " was left empty.");
            }
            try {
                // Adjust the format pattern to match expected incoming string formats
                return LocalDate.parse(textValue, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                throw new InvalidDateOfBirthException(identifyCell(cell) + " invalid date text format: " + textValue);
            }
        }

        throw new InvalidDateOfBirthException( identifyCell(cell) + " unsupported cell format for Date of Birth.");
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