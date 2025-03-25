package com.example.demo.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorMessage {
    private Timestamp timestamp;
    @JsonProperty("error_key")
    private Enums.ErrorKey statusCode;
    private String message;
    private Object data;
    private Boolean success;
}
