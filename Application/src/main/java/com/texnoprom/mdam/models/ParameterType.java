package com.texnoprom.mdam.models;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParameterType {

    private Integer parameterTypesId;
    private Integer devTypeId;
    private Boolean writable;
    private Integer parameterTypeClassId;
    private String paramTypeName;
    private Integer typeRelation;
    private String extraId;

    public ArrayList<String> getChildInfo() {
        ArrayList<String> list = new ArrayList<>();

        list.add("ID Типа: " + String.valueOf(devTypeId));
        list.add("Writable: " + String.valueOf(writable));
        list.add("Class ID: " + String.valueOf(parameterTypeClassId));
        list.add("Type Relation: " + String.valueOf(typeRelation));
        list.add("Extra ID: " + extraId);

        return list;
    }
}
