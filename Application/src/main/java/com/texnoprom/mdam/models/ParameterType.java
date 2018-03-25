package com.texnoprom.mdam.models;

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

}
