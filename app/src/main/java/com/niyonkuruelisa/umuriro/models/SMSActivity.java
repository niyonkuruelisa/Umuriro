package com.niyonkuruelisa.umuriro.models;

import java.util.Date;

public class SMSActivity {
    private String message;
    private boolean success = false;
    private Date date;

    public SMSActivity(String message, Date date, boolean success) {
        this.message = message;
        this.date = date;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
