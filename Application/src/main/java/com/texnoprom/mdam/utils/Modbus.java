package com.texnoprom.mdam.utils;


import com.texnoprom.mdam.models.Register;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.models.RegisterInfo;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//ToDo: Implement/use trird-party Modbus library instead of hardcoded commands
public class Modbus {
    // yyyy is the calendar year. YYYY is the week-year and is rejected by
    // SimpleDateFormat before Android 7, which leaves this class unloadable.
    private static SimpleDateFormat dateFormat;

    private static SimpleDateFormat dateFormat() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        }
        return dateFormat;
    }

    public static byte[] MEASURE = {1, 5, 0, 5, -1, 0, (byte) 156, 59};
    public static byte[] VALUES = {1, 3, 0, 0, 0, 10, (byte) 197, (byte) 205};
    public static byte[] CONFIG = {1, 3, 0, 10, 0, 5, (byte) 165, (byte) 203};
    public static byte[] ALL = {1, 3, 0, 0, 0, 41, (byte) 132, 20};
    public static byte[] CALIBRATIONS = {1, 3, 0, 15, 0, 15, (byte) 53, (byte) 205};
    public static byte[] MISC = {1, 3, 0, 30, 0, 11, (byte) 100, 11};
    /** Первые 12 измеряемых величин БКМ4. */
    public static byte[] BKM4_VALUES = readHolding(0, 12);
    /** Те же 12 величин, затем токи 1–8 и сопротивления 1–12. */
    public static byte[] BKM4_CHANNELS = readHolding(0, 32);

    public static byte[] readHolding(int start, int count) {
        byte[] command = new byte[8];
        command[0] = 1;
        command[1] = 3;
        command[2] = (byte) ((start >> 8) & 0xFF);
        command[3] = (byte) (start & 0xFF);
        command[4] = (byte) ((count >> 8) & 0xFF);
        command[5] = (byte) (count & 0xFF);
        byte[] crc = ModRTU_CRC(Arrays.copyOf(command, 6));
        command[6] = crc[0];
        command[7] = crc[1];
        return command;
    }

    /** Ответ функции 3 ровно на expectedCount регистров. Чужой кадр, включая команду измерения, даёт null. */
    public static short[] holdingValues(byte[] data, int expectedCount) {
        if (data == null || expectedCount <= 0 || data.length < 5 || (data[1] & 0xFF) != 3) {
            return null;
        }
        int byteCount = data[2] & 0xFF;
        if (byteCount != expectedCount * 2 || data.length < byteCount + 5) {
            return null;
        }
        short[] values = new short[expectedCount];
        ByteBuffer buffer = ByteBuffer.wrap(data, 3, byteCount);
        for (int i = 0; i < expectedCount; i++) {
            values[i] = buffer.getShort();
        }
        return values;
    }

    /** Ответ функции 6: адрес регистра и записанное значение. */
    public static int[] writtenRegister(byte[] data) {
        if (data == null || data.length < 6 || (data[1] & 0xFF) != 6) {
            return null;
        }
        int address = ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
        int value = ((data[4] & 0xFF) << 8) | (data[5] & 0xFF);
        return new int[]{address, value};
    }

    public static byte[] presetRegister(int slaveAddress, int registerAddress, int value) {
        byte[] command = new byte[8];
        command[0] = (byte) slaveAddress;
        command[1] = (byte) 6;
        byte[] byteAddress = ByteBuffer.allocate(4).putInt(registerAddress).array();
        command[2] = byteAddress[2];
        command[3] = byteAddress[3];
        byte[] byteValue = ByteBuffer.allocate(4).putInt(value).array();
        command[4] = byteValue[2];
        command[5] = byteValue[3];
        byte[] CRC = ModRTU_CRC(Arrays.copyOfRange(command, 0, 6));
        command[6] = CRC[0];
        command[7] = CRC[1];

        return command;
    }

    public static byte[] ModRTU_CRC(byte[] buf) {
        int crc = 0xFFFF;

        for (int pos = 0; pos < buf.length; pos++) {
            crc ^= (int) buf[pos] & 0xFF;

            for (int i = 8; i != 0; i--) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else                            // Else LSB is not set
                    crc >>= 1;                    // Just shift right
            }
        }
        byte reversed[] = new byte[2];
        reversed[0] = (byte) (crc & 0xff);
        reversed[1] = (byte) ((crc >> 8) & 0xff);


// Note, this number has low and high bytes swapped, so use it accordingly (or swap bytes)
        return reversed;
    }

    public static RegisterBatch RegistersFromData(byte[] data, String type, int firstRegister) {

        ArrayList<Register> registers = new ArrayList<>();
        registers.clear();
        String date = dateFormat().format(new Date());
        int command = data[1];
        int number = firstRegister;
        for (int i = 3; i < data.length - 2; i = i + 2) {
            byte val[] = Arrays.copyOfRange(data, i, i + 2);
            ByteBuffer wrapped = ByteBuffer.wrap(val);
            short value = wrapped.getShort();
            String rawName = RegisterInfo.Name(type, command, number);
            Register register = new Register(command, number, ParameterText.name(rawName),
                    displayValue(type, rawName, value));
            register.setUnit(unitOf(type, register, rawName));
            registers.add(register);
            number++;
        }
        return new RegisterBatch(date, type, registers);
    }

    /**
     * БКМ4 хранит «00.00» в сотых, «00.0» в десятых. Остальные регистры — целые без знака.
     * Отрицательный потенциал остаётся отрицательным.
     */
    public static float displayValue(String type, String name, short raw) {
        if (!"БКМ4".equals(type) || name == null) {
            return raw;
        }
        if (name.contains("00.00")) {
            return raw / 100f;
        }
        if (name.contains("00.0")) {
            return raw / 10f;
        }
        return raw & 0xFFFF;
    }

    /** Экран «Значения» БКМ1: регистр хранит сотые доли, знак остаётся как в модуле. */
    public static void applyBkm1ValueScale(List<Register> registers) {
        if (registers == null) {
            return;
        }
        for (Register register : registers) {
            register.setValue(register.getValue() * 0.01f);
        }
    }

    private static String unitOf(String type, Register register, String rawName) {
        String fromTable = RegisterInfo.Unit(register, type);
        if (fromTable != null) {
            String trimmed = fromTable.trim();
            if (trimmed.length() > 0 && !"?".equals(trimmed) && !trimmed.startsWith("No ")) {
                return trimmed;
            }
        }
        return ParameterText.unit(rawName);
    }

    public byte[] forceSingleCoil(byte address, short register, boolean value) {
        byte command[] = new byte[8];

        command[0] = address;
        command[1] = 5;
        //command.
        if (value) {
            command[4] = (byte) 255;
        }
        //byte CRC[]=ModRTU_CRC(command);

        return command;
    }
}
