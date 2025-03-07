package com.example.demo.Common;

public class ConvertSnakeToCamel {
    public static String snakeToCamel(String snake) {
        String[] parts = snake.split("_"); // Tách theo dấu "_"
        StringBuilder camelCase = new StringBuilder(parts[0]); // Giữ nguyên chữ đầu tiên

        for (int i = 1; i < parts.length; i++) {
            camelCase.append(parts[i].substring(0, 1).toUpperCase()) // Viết hoa chữ cái đầu
                    .append(parts[i].substring(1)); // Giữ nguyên phần còn lại
        }

        return camelCase.toString();
    }

}
