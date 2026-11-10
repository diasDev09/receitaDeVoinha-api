package com.receitadevoinha.api.exception;

import java.util.Map;

/**
 * Formato unico de erro da API: { "status": 400, "message": "...", "fields": { "title": "..." } }.
 * "fields" fica vazio quando o erro nao e de validacao de campos.
 */
public record ApiError(int status, String message, Map<String, String> fields) {

    public static ApiError of(int status, String message) {
        return new ApiError(status, message, Map.of());
    }
}
