package com.texnoprom.mdam.models;

import java.util.ArrayList;
import java.util.List;


public class RegisterBatch {
    private String timestamp;
    private String type;

    public List<Register> registers = new ArrayList<>();

    public RegisterBatch() {
    }

    public RegisterBatch(String timestamp, String type) {
        this.timestamp = timestamp;
        this.type = type;
        // this.BTRegisters = BTRegisters;
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

    public List<Register> getBTRegisters() {
        return registers;
    }

    public void setBTRegisters(List<Register> registers) {
        this.registers = registers;
    }
}
