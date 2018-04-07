package com.texnoprom.mdam.models;

import java.util.ArrayList;

/**
 * Created by Eekone on 4/8/2018.
 */

public interface ExpandableRowData {
    String getParentInfo();

    ArrayList<String> getChildInfo();
}
