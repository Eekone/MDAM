package com.texnoprom.mdam.models;

/**
 * Created by Eekone on 11/14/2017.
 */

public class Register {
    private int command;
    private int number;
    private String name;
    private float value;

    public Register(int command, int number, String name, float value) {
        this.command = command;
        this.number = number;
        this.name = name;
        this.value = value;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
