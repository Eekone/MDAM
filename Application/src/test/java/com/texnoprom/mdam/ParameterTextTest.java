package com.texnoprom.mdam;

import com.texnoprom.mdam.utils.ParameterText;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParameterTextTest {

    @Test
    public void dropsFixedPointFormatAndKeepsUnit() {
        assertEquals("Потенциал поляризационный канал-1",
                ParameterText.name("Потенциал поляризационный канал-1  \"00.00\" В"));
        assertEquals("В",
                ParameterText.unit("Потенциал поляризационный канал-1  \"00.00\" В"));
    }

    @Test
    public void dropsCurrentAndResistanceFormats() {
        assertEquals("Значение тока канал 1",
                ParameterText.name("Значение тока канал 1 \"00.0\" А"));
        assertEquals("А", ParameterText.unit("Значение тока канал 1 \"00.0\" А"));
        assertEquals("Сопротивление канала 12",
                ParameterText.name("Сопротивление канала 12  \"0000\" Ом"));
        assertEquals("Ом", ParameterText.unit("Сопротивление канала 12  \"0000\" Ом"));
    }

    @Test
    public void temperatureUnitIsDegrees() {
        assertEquals("Температура процессора",
                ParameterText.name("Температура процессора \"000\" С"));
        assertEquals("°C", ParameterText.unit("Температура процессора \"000\" С"));
    }
}
