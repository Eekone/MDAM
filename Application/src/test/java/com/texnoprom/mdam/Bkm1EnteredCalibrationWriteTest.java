package com.texnoprom.mdam;

import com.texnoprom.mdam.utils.Bkm1EnteredCalibrationWrite;
import com.texnoprom.mdam.utils.Modbus;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Bkm1EnteredCalibrationWriteTest {

    @Test
    public void screenRows16to30_areRegisters15to29() {
        assertEquals(15, Bkm1EnteredCalibrationWrite.FIRST);
        assertEquals(29, Bkm1EnteredCalibrationWrite.LAST);
    }

    @Test
    public void writesEachEnteredValueToItsRegister() {
        Bkm1EnteredCalibrationWrite write = new Bkm1EnteredCalibrationWrite();
        byte[] first = write.begin(new int[]{15, 16, 29}, new int[]{11, -4, 30});
        assertArrayEquals(Modbus.presetRegister(1, 15, 11), first);

        byte[] second = write.accept(first);
        assertTrue(write.consumed());
        assertArrayEquals(Modbus.presetRegister(1, 16, -4), second);

        byte[] third = write.accept(second);
        assertArrayEquals(Modbus.presetRegister(1, 29, 30), third);

        assertNull(write.accept(third));
        assertTrue(write.finished());
        assertFalse(write.isBusy());
    }
}
