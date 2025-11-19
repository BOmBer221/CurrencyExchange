package com.company.currencyexchange.exeption;

import java.time.LocalDateTime;


public class ExeptionResponse {
    private String message; // описание ошибки
    private int status; // код ошибки
    private String error;// расшисфорва кода
    private LocalDateTime timestamp;
    private String path; //URL запроса


    //конструктор ошибки
    public ExeptionResponse(String message, String error, String path, int status) {
        this.message = message;
        this.status = status;
        this.error = error;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {return status;}
    public LocalDateTime getTimestamp() {return timestamp;}
    public String getError() {return error;}
    public String getMessage() {return message;}
    public String getpath() {return path;}
}