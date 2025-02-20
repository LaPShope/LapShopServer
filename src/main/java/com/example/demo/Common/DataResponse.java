package com.example.demo.Common;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataResponse<T> {
    private boolean success;
    private String message;
    private T data;
}