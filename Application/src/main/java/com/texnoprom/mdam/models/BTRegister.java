package com.texnoprom.mdam.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BTRegister {
    private String timestamp;
    private String type;
    private int command;
    private int number;
    private String name;
    private float value;

    public BTRegister(String type, int command, int number, float value) {
        this.type = type;
        this.name = RegisterInfo.Name(type, command, number);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        this.timestamp = dateFormat.format(date);
        this.number = number;
        this.command = command;
        switch (type) {
            case "БКМ1":
                this.value = (number < 3 || number == 8) ? value / 100 : value;
                break;
            default:
                this.value = value;
        }
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getRegNumber() {
        return number;
    }

    public float getValue() {
        return value;
    }

    public int getCommand() {
        return command;
    }
}
