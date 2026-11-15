package com.texnoprom.mdam;

import com.texnoprom.mdam.models.FieldTask;
import com.texnoprom.mdam.services.TaskStore;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TaskStoreTest {

    @Test
    public void roundTrip_keepsTheTaskForItsOwner() {
        FieldTask task = new FieldTask("1", "Иван", "КП-12", "БКМ1", "снять потенциал", 1000L);
        List<FieldTask> loaded = TaskStore.decode(TaskStore.encode(list(task)));

        assertEquals(1, TaskStore.mine(loaded, "Иван").size());
        assertEquals("КП-12", loaded.get(0).getObjectName());
        assertEquals("БКМ1", loaded.get(0).getDeviceType());
        assertFalse(loaded.get(0).isDone());
        assertTrue(TaskStore.mine(loaded, "Пётр").isEmpty());
    }

    @Test
    public void markDone_changesOnlyTheChosenTask() {
        FieldTask first = new FieldTask("1", "Иван", "КП-12", "БКМ1", "", 1L);
        FieldTask second = new FieldTask("2", "Иван", "КП-13", "БКМ4", "", 2L);
        List<FieldTask> tasks = list(first, second);

        assertTrue(TaskStore.markDone(tasks, "2"));
        assertFalse(first.isDone());
        assertTrue(second.isDone());
        assertFalse(TaskStore.markDone(tasks, "2"));
    }

    @Test
    public void brokenText_isAnEmptyList() {
        assertTrue(TaskStore.decode("не задачи").isEmpty());
        assertTrue(TaskStore.knownDevice("ММПР"));
        assertFalse(TaskStore.knownDevice("другой"));
    }

    private static List<FieldTask> list(FieldTask first, FieldTask... rest) {
        List<FieldTask> tasks = new ArrayList<>();
        tasks.add(first);
        for (FieldTask task : rest) {
            tasks.add(task);
        }
        return tasks;
    }
}
