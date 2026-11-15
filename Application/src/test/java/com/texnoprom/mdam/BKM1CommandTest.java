package com.texnoprom.mdam;

import com.texnoprom.mdam.models.Register;
import com.texnoprom.mdam.utils.Modbus;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

//TODO: Rework according to Modbus changes
public class BKM1CommandTest {

    private static byte[] MEASURE = {1, 5, 0, 5, -1, 0, (byte) 156, 59};

    private static byte[] ALL = {1, 3, 0, 0, 0, 41, (byte) 132, 20};
    private static byte[] VALUES = {1, 3, 0, 0, 0, 10, (byte) 197, (byte) 205};
    private static byte[] CALIBRATIONS = {1, 3, 0, 15, 0, 15, (byte) 53, (byte) 205};
    private static byte[] CONFIG = {1, 3, 0, 10, 0, 5, (byte) 165, (byte) 203};
    private static byte[] MISC = {1, 3, 0, 30, 0, 11, (byte) 100, 11};

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

    @Test
    public void readHolding_matchesExistingValuesCommand() {
        assertArrayEquals(VALUES, Modbus.readHolding(0, 10));
    }

    @Test
    public void bkm4Channels_includeFirstTwelveReadings() {
        assertArrayEquals(Modbus.readHolding(0, 12), Modbus.BKM4_VALUES);
        assertArrayEquals(Modbus.readHolding(0, 32), Modbus.BKM4_CHANNELS);
    }

    @Test
    public void calibrationWrite_isReadBackFromTheSameRegister() {
        byte[] write = Modbus.presetRegister(1, 16, 94);
        assertEquals(6, write[1]);
        assertEquals(0, write[2]);
        assertEquals(16, write[3]);
        assertEquals(0, write[4]);
        assertEquals(94, write[5] & 0xFF);
        byte[] crc = Modbus.ModRTU_CRC(new byte[]{1, 6, 0, 16, 0, 94});
        assertEquals(crc[0], write[6]);
        assertEquals(crc[1], write[7]);

        int[] echoed = Modbus.writtenRegister(write);
        assertEquals(16, echoed[0]);
        assertEquals(94, echoed[1]);

        byte[] body = new byte[]{1, 3, 2, 0, 94};
        byte[] readCrc = Modbus.ModRTU_CRC(body);
        byte[] read = new byte[]{1, 3, 2, 0, 94, readCrc[0], readCrc[1]};
        assertEquals(94, Modbus.holdingValues(read, 1)[0]);
        assertNull(Modbus.holdingValues(read, 15));
        assertNull(Modbus.holdingValues(Modbus.MEASURE, 15));
    }

    @Test
    public void bkm1Values_scaleOnlyPotentialsTemperatureAndBattery() {
        Register protective = new Register(3, 0, "Потенциал защитный", -850);
        Register polarization = new Register(3, 1, "Поляризационный потенциал", 1050);
        Register current = new Register(3, 3, "Ток в трубопроводе", 250);
        Register temperature = new Register(3, 7, "Температура корпуса", 253);
        Register battery = new Register(3, 8, "Напряжение батареи питания", 380);
        Register door = new Register(3, 9, "Сопротивление датчика вскрытия", 400);
        Modbus.applyBkm1ValueScale(Arrays.asList(
                protective, polarization, current, temperature, battery, door));
        assertEquals(-8.5f, protective.getValue(), 0.001f);
        assertEquals(10.5f, polarization.getValue(), 0.001f);
        assertEquals(250f, current.getValue(), 0.001f);
        assertEquals(25.3f, temperature.getValue(), 0.001f);
        assertEquals(3.8f, battery.getValue(), 0.001f);
        assertEquals(400f, door.getValue(), 0.001f);
    }

    @Test
    public void bkm4DisplayValue_usesFixedPointFromTitle() {
        assertEquals(-1.23f, Modbus.displayValue("БКМ4", "Потенциал \"00.00\" В", (short) -123), 0.001f);
        assertEquals(10.5f, Modbus.displayValue("БКМ4", "Значение тока канал 1 \"00.0\" А", (short) 105), 0.001f);
        assertEquals(40000f, Modbus.displayValue("БКМ4", "Сопротивление канала 1 \"0000\" Ом", (short) 40000), 0.001f);
        assertEquals(100f, Modbus.displayValue("БКМ1", "Потенциал", (short) 100), 0.001f);
    }
}