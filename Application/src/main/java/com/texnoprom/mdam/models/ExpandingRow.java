package com.texnoprom.mdam.models;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class ExpandingRow implements Serializable {
    private String parentName;
    private ArrayList<String> childDataItems;
}
