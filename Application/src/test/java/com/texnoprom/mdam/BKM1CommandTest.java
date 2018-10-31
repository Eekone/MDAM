package com.texnoprom.mdam;

import com.texnoprom.mdam.utils.Modbus;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

//TODO: Rework according to Modbus changes
public class BKM1CommandTest {

    // private static byte[] MEASURE = {1, 5, 0, 5, -1, 0, (byte) 156, 59};
    // private static byte[] VALUES = {1, 3, 0, 0, 0, 10, (byte) 197, (byte) 205};
    // private static byte[] CONFIG = {1, 3, 0, 10, 0, 5, (byte) 165, (byte) 203};
    // private static byte[] ALL = {1, 3, 0, 0, 0, 60, (byte) 132, 20};
    // private static byte[] CALIBRATIONS = {1, 3, 0, 15, 0, 15, (byte) 53, (byte) 205};
    // private static byte[] MISC = {1, 3, 0, 30, 0, 11, (byte) 100, 11};

    private static byte[] MEASURE = {1, 5, 0, 5, -1, 0, (byte) 199, (byte) 183}; // Команда 5 измерить
    private static byte[] ALL = {1, 3, 0, 0, 0, 59, (byte) 222, (byte) 245}; // Группа аналоговые регистры
    private static byte[] VALUES = {1, 3, 0, 0, 0, 26, (byte) 239, (byte) 65}; //Группа измеряемые параметры
    private static byte[] CALIBRATIONS = {1, 3, 0, 31, 0, 46, (byte) 163, (byte) 231}; // Группа калибровки
    private static byte[] CONFIG = {1, 3, 0, 27, 0, 46, (byte) 198, (byte) 157}; // Группа режимы работы
    private static byte[] MISC = {1, 3, 0, 47, 0, 59, (byte) 233, 41};

    @Test
    public void measure_isCorrectCommand() throws Exception {
        assertArrayEquals(MEASURE, Modbus.MEASURE);
    }

    @Test
    public void all_isCorrectCommand() throws Exception {
        assertArrayEquals(ALL, Modbus.ALL);
    }

    @Test
    public void values_isCorrectCommand() throws Exception {
        assertArrayEquals(VALUES, Modbus.VALUES);
    }

    @Test
    public void calibrations_isCorrectCommand() throws Exception {
        assertArrayEquals(CALIBRATIONS, Modbus.CALIBRATIONS);
    }

    @Test
    public void config_isCorrectCommand() throws Exception {
        assertArrayEquals(CONFIG, Modbus.CONFIG);
    }

    @Test
    public void misc_isCorrectCommand() throws Exception {
        assertArrayEquals(MISC, Modbus.MISC);
    }
}