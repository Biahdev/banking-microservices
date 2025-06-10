package br.com.abeatrizdev.account_service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"timestamp", "status", "message", "fields"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(title = "Mensagem de erro")
public class ErrorMessage {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(title = "timestamp", example = "01/01/2024 21:10:10", type = "string", format = "date-time")
    private String timestamp;

    @Schema(title = "Código HTTP", example = "200", type = "int")
    private int status;

    @Schema(title = "Mensagem de erro", example = "200", type = "string")
    private String message;

    @Schema(title = "Lista dos campos com erro", type = "string")
    private Map<String, List<String>> details;

    public ErrorMessage(String message, HttpStatus status) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        this.status = status.value();
        this.message = message;
    }

    public ErrorMessage(String message, HttpStatus status, Map<String, List<String>> details) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        this.status = status.value();
        this.message = message;
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, List<String>> getDetails() {
        return details;
    }

    public void setDetails(Map<String, List<String>> details) {
        this.details = details;
    }
}
