package com.texnoprom.mdam;

import com.texnoprom.mdam.services.ArchiveWriter;
import com.texnoprom.mdam.services.ScreenForm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScreenFormTest {

    @Test
    public void bkm1_usesTheSameRowsAsTheModuleScreens() {
        assertEquals(0, ScreenForm.origin("БКМ1", ArchiveWriter.VALUES));
        assertEquals(10, ScreenForm.count("БКМ1", ArchiveWriter.VALUES));
        assertEquals(10, ScreenForm.origin("БКМ1", ArchiveWriter.CONFIG));
        assertEquals(5, ScreenForm.count("БКМ1", ArchiveWriter.CONFIG));
        assertEquals(15, ScreenForm.origin("БКМ1", ArchiveWriter.CALIBRATION));
        assertEquals(15, ScreenForm.count("БКМ1", ArchiveWriter.CALIBRATION));
        assertEquals(30, ScreenForm.origin("БКМ1", ArchiveWriter.MISC));
        assertEquals(11, ScreenForm.count("БКМ1", ArchiveWriter.MISC));
    }

    @Test
    public void bkm4_valuesAndCalibrationsMatchItsScreens() {
        assertEquals(12, ScreenForm.count("БКМ4", ArchiveWriter.VALUES));
        assertEquals(0, ScreenForm.origin("БКМ4", ArchiveWriter.CALIBRATION));
        assertEquals(32, ScreenForm.count("БКМ4", ArchiveWriter.CALIBRATION));
    }
}
