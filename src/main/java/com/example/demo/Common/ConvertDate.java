package com.example.demo.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConvertDate {
    private static final List<String> DATE_FORMATS = List.of(
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "MM-dd-yyyy",
            "yyyy/MM/dd"
    );

    public static Date convertToDate(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Date) {
            return (Date) value; // Nếu đã là Date thì giữ nguyên
        } else if (value instanceof Long) {
            return new Date((Long) value); // Nếu là timestamp (epoch time)
        } else if (value instanceof String) {
            return parseDateFromString((String) value);
        } else {
            throw new IllegalArgumentException("Unsupported date type: " + value.getClass().getSimpleName());
        }
    }

    private static Date parseDateFromString(String dateStr) {
        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false); // Ngăn nhập sai định dạng như 32/13/2023
                return sdf.parse(dateStr);
            } catch (ParseException ignored) {
                // Thử định dạng tiếp theo nếu lỗi
            }
        }
        throw new IllegalArgumentException("Invalid date format: " + dateStr + ". Supported formats: " + DATE_FORMATS);
    }
}
