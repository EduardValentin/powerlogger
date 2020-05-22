package com.example.powerlogger.dto.groups;

import com.google.gson.annotations.SerializedName;

public class GroupsAddExercisesStatus {
    private String code;
    private String message;

    GroupsAddExercisesStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
