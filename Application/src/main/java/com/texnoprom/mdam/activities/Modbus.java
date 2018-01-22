package com.texnoprom.mdam.activities;


import com.texnoprom.mdam.models.Register;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.models.RegisterInfo;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

//ToDo: Implement/use trird-party Modbus library instead of hardcoded commands
public class Modbus {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss", Locale.getDefault());

    public static byte[] MEASURE = {1, 5, 0, 5, -1, 0, (byte) 156, 59};
    public static byte[] VALUES = {1, 3, 0, 0, 0, 10, (byte) 197, (byte) 205};
    public static byte[] CONFIG = {1, 3, 0, 10, 0, 5, (byte) 165, (byte) 203};
    public static byte[] ALL = {1, 3, 0, 0, 0, 41, (byte) 132, 20};
    public static byte[] CALIBRATIONS = {1, 3, 0, 15, 0, 15, (byte) 53, (byte) 205};
    public static byte[] MISC = {1, 3, 0, 30, 0, 11, (byte) 100, 11};

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
            crc ^= (int) buf[pos] & 0xFF;   // XOR byte into least sig. byte of crc

            for (int i = 8; i != 0; i--) {    // Loop over each bit
                if ((crc & 0x0001) != 0) {      // If the LSB is set
                    crc >>= 1;                    // Shift right and XOR 0xA001
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
        String date = dateFormat.format(new Date());
        int command = data[1];
        int number = firstRegister;
        for (int i = 3; i < data.length - 2; i = i + 2) {
            byte val[] = Arrays.copyOfRange(data, i, i + 2);
            ByteBuffer wrapped = ByteBuffer.wrap(val);
            short value = wrapped.getShort();
            registers.add(new Register(command, number, RegisterInfo.Name(type, command, number), value));
            number++;
        }
        return new RegisterBatch(date, type, registers);
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
