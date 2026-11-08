package com.texnoprom.mdam.utils;

/**
 * БКМ1 после «Провести измерение» записывает одно число в регистры 15–26
 * (на экране это строки [16]–[27]). Сначала читаем эти регистры, затем
 * запускаем измерение и по ответу возвращаем прочитанные числа обратно.
 */
public final class Bkm1MeasureGuard {
    public static final int FIRST = 15;
    public static final int COUNT = 12;

    private short[] saved;
    private boolean readingSnapshot;
    private boolean waitingMeasure;
    private int restoreAt = -1;
    private boolean consumed;
    private boolean finished;

    public byte[] begin() {
        readingSnapshot = true;
        waitingMeasure = false;
        restoreAt = -1;
        consumed = false;
        finished = false;
        saved = null;
        return Modbus.readHolding(FIRST, COUNT);
    }

    public void cancel() {
        readingSnapshot = false;
        waitingMeasure = false;
        restoreAt = -1;
        consumed = false;
        finished = false;
        saved = null;
    }

    public boolean isBusy() {
        return readingSnapshot || waitingMeasure || restoreAt >= 0;
    }

    public boolean consumed() {
        return consumed;
    }

    public boolean finished() {
        return finished;
    }

    public byte[] accept(byte[] data) {
        consumed = false;
        finished = false;
        if (!isBusy() || data == null || data.length < 2) {
            return null;
        }
        if (readingSnapshot) {
            short[] values = Modbus.holdingValues(data, COUNT);
            if (values == null) {
                return null;
            }
            readingSnapshot = false;
            saved = values.clone();
            waitingMeasure = true;
            consumed = true;
            return Modbus.MEASURE;
        }
        if (waitingMeasure) {
            if ((data[1] & 0xFF) != 5) {
                return null;
            }
            waitingMeasure = false;
            consumed = true;
            if (saved == null) {
                return null;
            }
            restoreAt = 0;
            return writeAt(0);
        }
        if (restoreAt >= 0) {
            int[] written = Modbus.writtenRegister(data);
            if (written == null || written[0] != FIRST + restoreAt) {
                return null;
            }
            consumed = true;
            restoreAt++;
            if (restoreAt >= COUNT) {
                restoreAt = -1;
                finished = true;
                return null;
            }
            return writeAt(restoreAt);
        }
        return null;
    }

    private byte[] writeAt(int index) {
        return Modbus.presetRegister(1, FIRST + index, saved[index]);
    }
}
