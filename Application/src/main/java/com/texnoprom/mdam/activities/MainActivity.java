package com.texnoprom.mdam.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.texnoprom.mdam.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button connect = (Button) findViewById(R.id.connect_btn);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(MainActivity.this, DevicesActivity.class);
                MainActivity.this.startActivity(startMain);
            }
        });

        Button qr_btn = (Button) findViewById(R.id.qr_btn);
        qr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Наведите камеру на QR-код станции");
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.initiateScan();
            }
        });

        Button tasks = (Button) findViewById(R.id.tasks_btn);
        tasks.setEnabled(false);
        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        Button tct = (Button) findViewById(R.id.tct_button);
        tct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startTCT = new Intent(MainActivity.this, TCTActivity.class);
                MainActivity.this.startActivity(startTCT);
            }
        });

        Button archive = (Button) findViewById(R.id.archive_btn);
        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(MainActivity.this, ArchiveActivity.class);
                MainActivity.this.startActivity(startMain);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null)
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            else {
                switch (result.getContents()) {
                    case "ОПЕ11":
                    case "ОПЕ14": {
                        Intent startMain = new Intent(MainActivity.this, TCPActivity.class);
                        startMain.putExtra("deviceType", result.getContents());
                        MainActivity.this.startActivity(startMain);
                        break;
                    }
                    case "БКМ1":
                    case "БКМ4": {
                        Intent startMain = new Intent(MainActivity.this, BTActivity.class);
                        startMain.putExtra("deviceType", result.getContents());
                        MainActivity.this.startActivity(startMain);
                        break;
                    }
                    default:
                        Toast.makeText(this, "Устройство не найдено", Toast.LENGTH_LONG).show();
                        break;
                }
            }

        } else
            super.onActivityResult(requestCode, resultCode, data);

    }


}
