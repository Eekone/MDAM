package com.texnoprom.mdam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.texnoprom.mdam.R;

public class DevicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        Toolbar t = (Toolbar) findViewById(R.id.devicesToolbar);
        setSupportActionBar(t);

        Button OPE11 = (Button) findViewById(R.id.OPE11);
        OPE11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(DevicesActivity.this, TCPActivity.class);
                startMain.putExtra("deviceType", "ОПЕ11");
                DevicesActivity.this.startActivity(startMain);

            }
        });

        Button OPE14 = (Button) findViewById(R.id.OPE14);
        OPE14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(DevicesActivity.this, TCPActivity.class);
                startMain.putExtra("deviceType", "ОПЕ14");
                DevicesActivity.this.startActivity(startMain);

            }
        });

        Button BKM1 = (Button) findViewById(R.id.BKM1);
        BKM1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(DevicesActivity.this, BTActivity.class);
                startMain.putExtra("deviceType", "БКМ1");
                DevicesActivity.this.startActivity(startMain);
                finish();
            }
        });

        Button BKM4 = (Button) findViewById(R.id.BKM4);
        BKM4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(DevicesActivity.this, BTActivity.class);
                startMain.putExtra("deviceType", "БКМ4");
                DevicesActivity.this.startActivity(startMain);
                finish();
            }
        });


        Button MMPR = (Button) findViewById(R.id.MMPR);
        MMPR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(DevicesActivity.this, TCPActivity.class);
                startMain.putExtra("deviceType", "ММПР");
                DevicesActivity.this.startActivity(startMain);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dlogin:
                Intent exit = new Intent(DevicesActivity.this, LoginActivity.class);
                DevicesActivity.this.startActivity(exit);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
    }
}
