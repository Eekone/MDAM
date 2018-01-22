package com.texnoprom.mdam;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.texnoprom.mdam.activities.Modbus;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.models.RegisterInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class BKM1DataParsingTest {

    private static byte valuesInput[] = {1, 3, 20, 0, 0, 0, 1, 0, 0, 0, 1, 3, -56, 3, -56, 3, -56, 0, 31, 1, -98, 3, -56, 24, -76};
    private static byte configInput[] = {1, 3, 10, 0, 1, 37, -128, 0, 0, 0, 0, 0, 0, 106, -55};
    private static byte calibrationInput[] = {1, 3, 30, 0, 0, 0, 28, 1, -12, 0, 0, 0, 78, 1, -12, 0, 0, 0, 78, 1, -12, 0, 0, 1, 37, 0, 101, 0, 0, 0, 11, 11, -72, -90, -97};
    private static byte miscInput[] = {1, 3, 22, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, -31, 0, 1, 0, 1, 0, 0, 0, -56, -84, 46};

    @Before
    public void setup() {
        RegisterInfo.setContext(InstrumentationRegistry.getTargetContext());
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Test
    public void valuesParsing_isCorrect() throws Exception {
        compare(valuesInput, "valuesRegisters.json", 0);
    }

    @Test
    public void configParsing_isCorrect() throws Exception {
        compare(configInput, "configRegisters.json", 10);
    }

    @Test
    public void calibrationParsing_isCorrect() throws Exception {
        compare(calibrationInput, "calibrationRegisters.json", 15);
    }

    @Test
    public void miscParsing_isCorrect() throws Exception {
        compare(miscInput, "miscRegisters.json", 30);
    }

    private void compare(byte[] inputData, String expectedFilePath, int firstRegister) throws Exception {
        RegisterBatch registerList = Modbus.RegistersFromData(inputData, "БКМ1", firstRegister);

        JsonParser jsonParser = new JsonParser();
        JsonElement actual = jsonParser.parse(new Gson().toJson(registerList.getRegisters()));

        InputStream inputStream = this.getClass()
                .getClassLoader().getResourceAsStream(expectedFilePath);
        JsonElement expected = jsonParser.parse(convertStreamToString(inputStream));

        assertEquals(expected, actual);
    }
}