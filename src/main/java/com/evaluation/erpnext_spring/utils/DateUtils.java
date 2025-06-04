package com.evaluation.erpnext_spring.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class DateUtils {

    // private static final List<String> DATE_FORMATS = Arrays.asList(
    //     "dd/MM/yyyy",
    //     "dd-MM-yyyy",
    //     "yyyy/MM/dd",
    //     "yyyy-MM-dd",
    //     "MM/dd/yyyy",
    //     "MM-dd-yyyy",
    //     "dd.MM.yyyy",
    //     "yyyy.MM.dd",
    //     "MMM dd, yyyy",
    //     "dd MMM yyyy",
    //     "MMMM dd, yyyy",
    //     "dd MMMM yyyy"
    // );

    // public static boolean isValidDate(String dateStr) {
    //     if (dateStr == null || dateStr.trim().isEmpty()) {
    //         return false;
    //     }

    //     for (String format : DATE_FORMATS) {
    //         try {
    //             DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format)
    //                     .withResolverStyle(ResolverStyle.STRICT);
    //             LocalDate.parse(dateStr, formatter);
    //             return true;
    //         } catch (DateTimeParseException e) {
    //             // Continue to next format
    //         }
    //     }
    //     return false;
    // }

    // public static String normalizeDate(String dateStr) throws DateTimeParseException {
    //     if (dateStr == null || dateStr.trim().isEmpty()) {
    //         throw new IllegalArgumentException("Date string cannot be null or empty");
    //     }

    //     for (String format : DATE_FORMATS) {
    //         try {
    //             DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format)
    //                     .withResolverStyle(ResolverStyle.STRICT);
    //             LocalDate date = LocalDate.parse(dateStr, formatter);
    //             return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    //         } catch (DateTimeParseException e) {
    //             // Continue to next format
    //         }
    //     }
    //     throw new DateTimeParseException("Unable to parse date: " + dateStr, dateStr, 0);
    // }

    // // Exemple d'utilisation
    // public static void main(String[] args) {
    //     String[] testDates = {
    //         "31/12/2023", "12-31-2023", "2023.12.31",
    //         "Dec 31, 2023", "31 December 2023", "invalid"
    //     };

    //     for (String date : testDates) {
    //         System.out.println("Date: " + date);
    //         System.out.println("Valid: " + isValidDate(date));
    //         if (isValidDate(date)) {
    //             try {
    //                 System.out.println("Normalized: " + normalizeDate(date));
    //             } catch (Exception e) {
    //                 System.out.println("Normalization failed: " + e.getMessage());
    //             }
    //         }
    //         System.out.println("-----");
    //     }
    // }

    private static final DateTimeFormatter FORMAT_DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_YYYYMMDD = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final Pattern PATTERN_DDMMYYYY = Pattern.compile("^(\\d{2})/(\\d{2})/(\\d{4})$");
    private static final Pattern PATTERN_YYYYMMDD = Pattern.compile("^(\\d{4})/(\\d{2})/(\\d{2})$");

    public static boolean isValidDate(String dateStr) {
        return normalizeToStandardFormat(dateStr) != null;
    }

    public static LocalDate normalizeToStandardFormat(String dateStr) {
        try {
            if (PATTERN_DDMMYYYY.matcher(dateStr).matches()) {
                return validateAndParseDDMMYYYY(dateStr);
            }
            else if (PATTERN_YYYYMMDD.matcher(dateStr).matches()) {
                return validateAndParseYYYYMMDD(dateStr);
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static LocalDate validateAndParseDDMMYYYY(String dateStr) {
        String[] parts = dateStr.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (!isValidDateComponents(year, month, day)) {
            return null;
        }
        return LocalDate.parse(dateStr, FORMAT_DDMMYYYY);
    }

    private static LocalDate validateAndParseYYYYMMDD(String dateStr) {
        String[] parts = dateStr.split("/");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        if (!isValidDateComponents(year, month, day)) {
            return null;
        }

        return LocalDate.parse(dateStr, FORMAT_YYYYMMDD);
    }

    private static boolean isValidDateComponents(int year, int month, int day) {
        if (month < 1 || month > 12 || day < 1) {
            return false;
        }

        int maxDay;
        switch (month) {
            case 2: maxDay = isLeapYear(year) ? 29 : 28; break;
            case 4: case 6: case 9: case 11: maxDay = 30; break;
            default: maxDay = 31;
        }

        return day <= maxDay;
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}


