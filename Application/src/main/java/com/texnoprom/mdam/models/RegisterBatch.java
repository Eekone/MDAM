package com.texnoprom.mdam.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class RegisterBatch {
    private String timestamp;
    private String type;

    private List<Register> registers = new ArrayList<>();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd:mm:YYYY HH:mm:ss", Locale.getDefault());

    public RegisterBatch() {
        this.timestamp = dateFormat.format(new Date());
    }

    public RegisterBatch(String timestamp, String type, List<Register> registers) {
        this.timestamp = timestamp;
        this.type = type;
        this.registers = registers;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Register> getRegisters() {
        return registers;
    }

    public void setRegisters(List<Register> registers) {
        this.registers = registers;
    }
}
