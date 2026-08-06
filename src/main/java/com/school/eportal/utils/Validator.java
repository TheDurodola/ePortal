package com.school.eportal.utils;

import com.school.eportal.exceptions.InvalidFileTypeException;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.jspecify.annotations.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    private static final Detector DETECTOR = TikaConfig.getDefaultConfig().getDetector();
    private static final Pattern SESSION_PATTERN = Pattern.compile("\\b\\d{4}/(?:\\d{2}|\\d{4})\\b");

    // Set of valid MIME types for Microsoft Excel spreadsheets
    private static final Set<String> EXCEL_MIME_TYPES = Set.of(
            "application/vnd.ms-excel",                                               // .xls (Legacy OLE2)
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",      // .xlsx (OOXML)
            "application/vnd.ms-excel.sheet.macroEnabled.12",                        // .xlsm
            "application/vnd.ms-excel.template.macroEnabled.12",                     // .xltm
            "application/vnd.openxmlformats-officedocument.spreadsheetml.template", // .xltx
            "application/vnd.ms-excel.sheet.binary.macroEnabled.12"                  // .xlsb
    );

    public static boolean isValidSessionFormat(String input) {
        if (input == null) {
            return false;
        }
        Matcher matcher = SESSION_PATTERN.matcher(input);
        return matcher.matches();
    }

    public static boolean isExcelFile(InputStream inputStream, String originalFilename) throws IOException {
        Metadata metadata = new Metadata();
        if (originalFilename != null && !originalFilename.isBlank()) {
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, originalFilename);
        }

        try (TikaInputStream tikaStream = TikaInputStream.get(inputStream)) {
            MediaType mediaType = DETECTOR.detect(tikaStream, metadata);
            String detectedType = mediaType.toString();

            return EXCEL_MIME_TYPES.contains(detectedType);
        }
    }


    public static void validateExcelFile(MultipartFile file) throws IOException {
        if (!isExcelFile(file.getInputStream(), file.getOriginalFilename())) {
            throw new InvalidFileTypeException("Invalid file type: File is not a valid Microsoft Excel document.");
        }
    }
}
