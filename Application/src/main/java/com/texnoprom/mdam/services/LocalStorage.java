package com.texnoprom.mdam.services;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.utils.Modbus;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by texnoprom on 17.03.2017.
 */

public abstract class LocalStorage {
    public static void saveData(byte[] data, Context context) {
        RegisterBatch ar = Modbus.RegistersFromData(data, "БКМ1", 0);
        FileWriter f;
        File myFile = new File(Environment.getExternalStorageDirectory() + "/mysdfile.txt");
        try {
            f = new FileWriter(myFile, true);
            f.write(DateFormat.getDateTimeInstance().format(new Date()) + "\n" + "Имя" + "\n");
            for (int i = 0; i < ar.getRegisters().size(); i++) {
                f.write(Float.toString(ar.getRegisters().get(i).getValue()) + "\t\t\t");
            }
            f.write("\n\n");
            f.flush();
            f.close();
            Toast.makeText(context,
                    "Сохранено в 'mysdfile.txt'",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
