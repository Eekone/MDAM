package com.texnoprom.mdam.models;

import java.util.ArrayList;

import lombok.Data;

@Data
public class CustomData {

    private String readableParamValuesId;
    private String sessionBeginDateTime;
    private String objName;
    private String paramName;
    private String paramValueTxt;
    private String measUnit;

    public ArrayList<String> getChildInfo() {
        ArrayList<String> list = new ArrayList<>();

        list.add("ID: " + readableParamValuesId);
        list.add("Имя параметра: " + paramName);
        list.add("Имя объекта: " + objName);
        list.add("Значение: " + paramValueTxt);
        list.add("Ед. измерения: " + measUnit);

        return list;
    }
}
