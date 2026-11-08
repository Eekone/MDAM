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

        assertArrayEquals(Modbus.readHolding(15, 12), guard.begin());
        assertTrue(guard.isBusy());
        assertNull(guard.accept(Modbus.MEASURE));
        assertFalse(guard.consumed());

        byte[] firstWrite = guard.accept(readFrame(known));
        assertTrue(guard.consumed());
        assertArrayEquals(Modbus.MEASURE, firstWrite);
        firstWrite = guard.accept(Modbus.MEASURE);
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
    public void cancel_forgetsAnUnfinishedMeasurement() {
        Bkm1MeasureGuard guard = new Bkm1MeasureGuard();
        guard.begin();
        guard.cancel();
        assertFalse(guard.isBusy());
        assertNull(guard.accept(readFrame(new short[Bkm1MeasureGuard.COUNT])));
        assertFalse(guard.consumed());
    }

    @Test
    public void withoutAVisibleList_readsTheBlockBeforeMeasuring() {
        Bkm1MeasureGuard guard = new Bkm1MeasureGuard();
        assertArrayEquals(Modbus.readHolding(15, 12), guard.begin());

        short[] values = new short[12];
        values[0] = 7;
        for (int i = 1; i < values.length; i++) {
            values[i] = (short) (10 * i);
        }

        assertArrayEquals(Modbus.MEASURE, guard.accept(readFrame(values)));
        assertArrayEquals(Modbus.presetRegister(1, 15, 7), guard.accept(Modbus.MEASURE));
    }

    private static byte[] readFrame(short[] values) {
        byte[] body = new byte[3 + values.length * 2];
        body[0] = 1;
        body[1] = 3;
        body[2] = (byte) (values.length * 2);
        ByteBuffer data = ByteBuffer.wrap(body, 3, values.length * 2);
        for (short value : values) {
            data.putShort(value);
        }
        return Arrays.copyOf(body, body.length + 2);
    }
}
