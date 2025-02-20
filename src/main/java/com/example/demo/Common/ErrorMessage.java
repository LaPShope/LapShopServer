package com.example.demo.Common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Data
public class ErrorMessage {
    private Timestamp timestamp;
    @JsonProperty("error_key")
    private Enum<Enums.ErrorKey> statusCode;
    private String message;
    private Object data;
    private Boolean success;
}