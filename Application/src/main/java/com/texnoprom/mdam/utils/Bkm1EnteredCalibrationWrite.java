package com.texnoprom.mdam.utils;

/**
 * Строки [16]–[30] — регистры 15–29. По очереди пишет в модуль числа с экрана.
 */
public final class Bkm1EnteredCalibrationWrite {
    public static final int FIRST = 15;
    public static final int LAST = 29;

    private int[] addresses = new int[0];
    private int[] values = new int[0];
    private int index = -1;
    private boolean consumed;
    private boolean finished;

    public byte[] begin(int[] addresses, int[] values) {
        consumed = false;
        finished = false;
        if (addresses == null || values == null || addresses.length == 0
                || addresses.length != values.length) {
            this.addresses = new int[0];
            this.values = new int[0];
            index = -1;
            return null;
        }
        this.addresses = addresses.clone();
        this.values = values.clone();
        index = 0;
        return commandAt(0);
    }

    public void cancel() {
        index = -1;
        consumed = false;
        finished = false;
    }

    public boolean isBusy() {
        return index >= 0;
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
        if (!isBusy()) {
            return null;
        }
        int[] written = Modbus.writtenRegister(data);
        if (written == null || written[0] != addresses[index]) {
            return null;
        }
        consumed = true;
        index++;
        if (index >= addresses.length) {
            index = -1;
            finished = true;
            return null;
        }
        return commandAt(index);
    }

    private byte[] commandAt(int at) {
        return Modbus.presetRegister(1, addresses[at], values[at]);
    }
}
