package com.texnoprom.mdam;

import com.texnoprom.mdam.models.Register;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.services.LocalArchive;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocalArchiveTest {

    @Test
    public void roundTrip_keepsDeviceTimeAndValues() {
        RegisterBatch batch = sample("БКМ1", "15/11/2026 17:40:00", 3.8f);
        List<RegisterBatch> loaded = LocalArchive.decode(LocalArchive.encode(list(batch)));

        assertEquals(1, loaded.size());
        assertEquals("БКМ1", loaded.get(0).getType());
        assertEquals("15/11/2026 17:40:00", loaded.get(0).getTimestamp());
        assertEquals(3.8f, loaded.get(0).getRegisters().get(0).getValue(), 0.001f);
        assertEquals("В", loaded.get(0).getRegisters().get(0).getUnit());
    }

    @Test
    public void emptyAndBrokenText_becomeAnEmptyArchive() {
        assertTrue(LocalArchive.decode("").isEmpty());
        assertTrue(LocalArchive.decode("не архив").isEmpty());
    }

    @Test
    public void batchWithoutRegisters_isNotStored() {
        assertFalse(LocalArchive.keep(new RegisterBatch("БКМ1")));
        assertTrue(LocalArchive.keep(sample("БКМ4", "15/11/2026 17:41:00", 12f)));
    }

    private static RegisterBatch sample(String type, String time, float value) {
        List<com.texnoprom.mdam.models.Register> registers = new ArrayList<>();
        Register register = new Register(3, 8, "Напряжение батареи питания", value);
        register.setUnit("В");
        registers.add(register);
        return new RegisterBatch(time, type, registers);
    }

    private static List<RegisterBatch> list(RegisterBatch batch) {
        List<RegisterBatch> batches = new ArrayList<>();
        batches.add(batch);
        return batches;
    }
}
