package com.texnoprom.mdam;

import com.texnoprom.mdam.utils.Bkm1MeasureGuard;
import com.texnoprom.mdam.utils.Modbus;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Bkm1MeasureGuardTest {

    @Test
    public void screenRows16to27_areRegisters15to26() {
        assertEquals(15, Bkm1MeasureGuard.FIRST);
        assertEquals(26, Bkm1MeasureGuard.FIRST + Bkm1MeasureGuard.COUNT - 1);
    }

    @Test
    public void measure_restoresEachCalibrationTheModuleHadBefore() {
        Bkm1MeasureGuard guard = new Bkm1MeasureGuard();
        short[] known = new short[Bkm1MeasureGuard.COUNT];
        for (int i = 0; i < known.length; i++) {
            known[i] = (short) (100 + i);
        }
        known[1] = -40;

        assertArrayEquals(Modbus.MEASURE, guard.begin(known));
        assertTrue(guard.isBusy());

        byte[] firstWrite = guard.accept(Modbus.MEASURE);
        assertTrue(guard.consumed());
        assertArrayEquals(Modbus.presetRegister(1, 15, 100), firstWrite);

        byte[] next = firstWrite;
        for (int i = 0; i < known.length; i++) {
            int address = Bkm1MeasureGuard.FIRST + i;
            assertArrayEquals(Modbus.presetRegister(1, address, known[i]), next);
            next = guard.accept(next);
        }
        assertNull(next);
        assertTrue(guard.finished());
        assertFalse(guard.isBusy());
    }

    @Test
    public void withoutAVisibleList_readsTheBlockBeforeMeasuring() {
        Bkm1MeasureGuard guard = new Bkm1MeasureGuard();
        assertArrayEquals(Modbus.readHolding(15, 12), guard.begin(null));

        byte[] body = new byte[3 + 24];
        body[0] = 1;
        body[1] = 3;
        body[2] = 24;
        ByteBuffer data = ByteBuffer.wrap(body, 3, 24);
        data.putShort((short) 7);
        for (int i = 1; i < 12; i++) {
            data.putShort((short) (10 * i));
        }
        byte[] frame = Arrays.copyOf(body, body.length + 2);

        assertArrayEquals(Modbus.MEASURE, guard.accept(frame));
        assertArrayEquals(Modbus.presetRegister(1, 15, 7), guard.accept(Modbus.MEASURE));
    }
}
