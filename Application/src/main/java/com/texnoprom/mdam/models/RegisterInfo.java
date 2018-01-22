package com.texnoprom.mdam.models;

import android.content.Context;
import android.content.res.Resources;

import com.texnoprom.mdam.R;

//TODO: Use maps instead
public abstract class RegisterInfo {
    private static String[] BKM1Titles;
    private static String[] BKM1Units;
    private static String[] BKM4Titles;
    private static String[] MMPRTitles;
    private static String[] MMPRUnits;
    private static String[] OPE11_2Titles;
    private static String[] OPE11_3Titles;
    private static String[] OPE11_4Titles;

    private static String[] MMPRSections;
    private static String[] OPE11_Sections;

    private static boolean contextPassed = false;

    public static void setContext(Context appContext) {
        contextPassed = true;
        Resources res = appContext.getResources();
        BKM1Titles = res.getStringArray(R.array.BKM1_3_titles);
        BKM1Units = res.getStringArray(R.array.BKM1_units);
        BKM4Titles = res.getStringArray(R.array.BKM4_titles);
        MMPRTitles = res.getStringArray(R.array.MMPR_3_titles);
        MMPRUnits = res.getStringArray(R.array.MMPR_register_units);
        MMPRSections = res.getStringArray(R.array.MMPR_sections_titles);
        OPE11_Sections = res.getStringArray(R.array.OPE11_sections_titles);

        OPE11_2Titles = res.getStringArray(R.array.OPE11_2_titles);
        OPE11_3Titles = res.getStringArray(R.array.OPE11_3_titles);
        OPE11_4Titles = res.getStringArray(R.array.OPE11_4_titles);
    }

    public static String Name(Register register, String type) {
        if (!contextPassed) return "No Context!";

        try {
            switch (register.getCommand()) {
                case 2:
                    switch (type) {
                        case "ОПЕ11":
                            return OPE11_2Titles[register.getNumber()];
                        default:
                            return "No Such Type!";

                    }

                case 3:
                    switch (type) {
                        case "БКМ1":
                            return BKM1Titles[register.getNumber()];
                        case "БКМ4":
                            return BKM4Titles[register.getNumber()];
                        case "ММПР":
                            return MMPRTitles[register.getNumber()];
                        case "ОПЕ11":
                            return OPE11_3Titles[register.getNumber()];
                        default:
                            return "No Such Type!";
                    }
                case 4:
                    switch (type) {
                        case "ОПЕ11":
                            return OPE11_4Titles[register.getNumber()];
                        default:
                            return "No Such Type!";

                    }
                default:
                    return "No Such Type!";
            }
        } catch (NullPointerException ex) {
            return "No Info!";
        }
    }

    public static String Name(String type, int command, int number) {
        if (!contextPassed) return "No Context!";

        try {
            switch (command) {
                case 2:
                    switch (type) {
                        case "ОПЕ11":
                            return OPE11_2Titles[number];
                        default:
                            return "No Such Type!";

                    }

                case 3:
                    switch (type) {
                        case "БКМ1":
                            return BKM1Titles[number];
                        case "БКМ4":
                            return BKM4Titles[number];
                        case "ММПР":
                            return MMPRTitles[number];
                        case "ОПЕ11":
                            return OPE11_3Titles[number];
                        default:
                            return "No Such Type!";
                    }
                case 4:
                    switch (type) {
                        case "ОПЕ11":
                            return OPE11_4Titles[number];
                        default:
                            return "No Such Type!";

                    }
                default:
                    return "No Such Type!";
            }
        } catch (NullPointerException ex) {
            return "No Info!";
        }
    }


    public static String Unit(Register register, String type) {
        if (!contextPassed) return "No Context!";

        try {
            switch (register.getCommand()) {
                case 2:
                    return "";
                case 3:
                    switch (type) {
                        case "БКМ1":
                            return BKM1Units[register.getNumber()];
                        case "ММПР":
                            return MMPRUnits[register.getNumber()];
                        default:
                            return "?";
                    }
                case 4:
                    return "";
                default:
                    return "";
            }
        } catch (NullPointerException ex) {
            return "No Info!";
        }
    }

    public static String[] sections(String device) {
        switch (device) {
            case "ММПР":
                return MMPRSections;
            case "ОПЕ11":
                return OPE11_Sections;
            default:
                return null;
        }
    }
}
