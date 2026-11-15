package com.texnoprom.mdam.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.models.Register;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.services.ApiHelper;
import com.texnoprom.mdam.services.ArchiveWriter;
import com.texnoprom.mdam.services.LocalArchive;
import com.texnoprom.mdam.services.TaskStore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArchiveActivity extends AppCompatActivity {

    private RecyclerView list;
    private TextView empty;
    private TextView meta;
    private List<RegisterBatch> batches = new ArrayList<>();
    private RegisterBatch opened;
    private int openedIndex = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        android.support.v7.widget.Toolbar toolbar =
                (android.support.v7.widget.Toolbar) findViewById(R.id.archiveToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        list = (RecyclerView) findViewById(R.id.archive_list);
        empty = (TextView) findViewById(R.id.archive_empty);
        meta = (TextView) findViewById(R.id.archive_meta);
        list.setLayoutManager(new LinearLayoutManager(this));
        showList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (opened == null) {
            showList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_archive, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean record = opened != null;
        menu.findItem(R.id.archive_fill).setVisible(!record);
        menu.findItem(R.id.archive_register).setVisible(record);
        menu.findItem(R.id.archive_send).setVisible(record);
        menu.findItem(R.id.archive_delete).setVisible(record);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goBack();
            return true;
        }
        if (item.getItemId() == R.id.archive_fill) {
            showFillDialog();
            return true;
        }
        if (item.getItemId() == R.id.archive_register) {
            showRegisterDialog();
            return true;
        }
        if (item.getItemId() == R.id.archive_send) {
            sendOpened();
            return true;
        }
        if (item.getItemId() == R.id.archive_delete) {
            confirmDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (opened != null) {
            showList();
            return;
        }
        super.onBackPressed();
    }

    private void goBack() {
        if (opened != null) {
            showList();
        } else {
            finish();
        }
    }

    private void showList() {
        opened = null;
        openedIndex = -1;
        batches = LocalArchive.load(this);
        setTitle("Локальный архив");
        meta.setVisibility(View.GONE);
        placeList(false);
        empty.setVisibility(batches.isEmpty() ? View.VISIBLE : View.GONE);
        list.setVisibility(batches.isEmpty() ? View.GONE : View.VISIBLE);
        list.setAdapter(new BatchAdapter());
        invalidateOptionsMenu();
    }

    private void showBatch(int index) {
        if (index < 0 || index >= batches.size()) {
            showList();
            return;
        }
        openedIndex = index;
        opened = batches.get(index);
        String type = opened.getType() == null ? "Запись" : opened.getType();
        if (text(opened.getScreen()).length() > 0) {
            type = type + " · " + opened.getScreen();
        }
        setTitle(type);
        String who = text(opened.getUser());
        meta.setText((who.length() == 0 ? "Пользователь не указан" : who) + " · " + opened.getTimestamp());
        meta.setVisibility(View.VISIBLE);
        placeList(true);
        empty.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        list.setAdapter(new RegisterRows(opened.getRegisters()));
        invalidateOptionsMenu();
    }

    private void sendOpened() {
        if (opened == null) {
            return;
        }
        new ApiHelper(this).postRegisters(opened, new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                String text;
                switch (response.code()) {
                    case 201:
                        text = "Успешно загружено";
                        break;
                    case 409:
                        text = "Регистры уже были загружены";
                        break;
                    default:
                        text = "Ошибка";
                        break;
                }
                Toast.makeText(ArchiveActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(ArchiveActivity.this, "Нет ответа", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void placeList(boolean belowMeta) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) list.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, belowMeta ? R.id.archive_meta : R.id.archive_toolbar);
        list.setLayoutParams(params);
    }

    private void showFillDialog() {
        final EditText user = new EditText(this);
        user.setSingleLine(true);
        user.setHint("Пользователь");
        user.setText(ArchiveWriter.currentUser(this));
        final EditText time = new EditText(this);
        time.setSingleLine(true);
        time.setHint("дд/ММ/гггг чч:мм:сс");
        time.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
        final Spinner device = new Spinner(this);
        ArrayAdapter<String> devices = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TaskStore.DEVICES);
        devices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        device.setAdapter(devices);
        final Spinner screen = new Spinner(this);
        ArrayAdapter<String> screens = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ArchiveWriter.SCREENS);
        screens.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        screen.setAdapter(screens);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        box.setPadding(pad, pad, pad, 0);
        box.addView(user);
        box.addView(time);
        box.addView(device);
        box.addView(screen);

        new AlertDialog.Builder(this)
                .setTitle("Запись в архив")
                .setView(box)
                .setPositiveButton("Сохранить", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        String who = user.getText().toString().trim();
                        String when = time.getText().toString().trim();
                        if (who.length() == 0 || when.length() == 0) {
                            Toast.makeText(ArchiveActivity.this,
                                    "Укажите пользователя и метку времени", Toast.LENGTH_LONG).show();
                            return;
                        }
                        RegisterBatch batch = new RegisterBatch(when, (String) device.getSelectedItem(),
                                new ArrayList<Register>());
                        batch.setId(UUID.randomUUID().toString());
                        batch.setUser(who);
                        batch.setScreen((String) screen.getSelectedItem());
                        if (!LocalArchive.addAny(ArchiveActivity.this, batch)) {
                            Toast.makeText(ArchiveActivity.this, "Не удалось сохранить запись", Toast.LENGTH_LONG).show();
                            return;
                        }
                        showList();
                        openById(batch.getId());
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showRegisterDialog() {
        if (opened == null) {
            return;
        }
        final EditText number = new EditText(this);
        number.setSingleLine(true);
        number.setInputType(InputType.TYPE_CLASS_NUMBER);
        number.setHint("Номер строки, с 1");
        final EditText name = new EditText(this);
        name.setSingleLine(true);
        name.setHint("Название");
        final EditText value = new EditText(this);
        value.setSingleLine(true);
        value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                | InputType.TYPE_NUMBER_FLAG_SIGNED);
        value.setHint("Значение");
        final EditText unit = new EditText(this);
        unit.setSingleLine(true);
        unit.setHint("Единица");

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        box.setPadding(pad, pad, pad, 0);
        box.addView(number);
        box.addView(name);
        box.addView(value);
        box.addView(unit);

        new AlertDialog.Builder(this)
                .setTitle("Регистр")
                .setView(box)
                .setPositiveButton("Добавить", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        addRegister(number.getText().toString().trim(), name.getText().toString().trim(),
                                value.getText().toString().trim(), unit.getText().toString().trim());
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void addRegister(String numberText, String name, String valueText, String unit) {
        if (opened == null) {
            return;
        }
        int shown;
        float reading;
        try {
            shown = Integer.parseInt(numberText);
            reading = Float.parseFloat(valueText.replace(',', '.'));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Введите номер строки и значение", Toast.LENGTH_LONG).show();
            return;
        }
        if (shown < 1) {
            Toast.makeText(this, "Номер строки начинается с 1", Toast.LENGTH_LONG).show();
            return;
        }
        if (opened.getRegisters() == null) {
            opened.setRegisters(new ArrayList<Register>());
        }
        Register register = new Register(3, shown - 1, name, reading);
        register.setUnit(unit);
        opened.getRegisters().add(register);
        if (opened.getId() == null) {
            opened.setId(UUID.randomUUID().toString());
        }
        LocalArchive.replace(this, opened);
        openById(opened.getId());
    }

    private void openById(String id) {
        batches = LocalArchive.load(this);
        for (int i = 0; i < batches.size(); i++) {
            if (id != null && id.equals(batches.get(i).getId())) {
                showBatch(i);
                return;
            }
        }
        showList();
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }

    private void confirmDelete() {
        if (opened == null) {
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("Удалить эту запись из архива?")
                .setPositiveButton("Удалить", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        LocalArchive.remove(ArchiveActivity.this, openedIndex);
                        showList();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private class BatchAdapter extends RecyclerView.Adapter<BatchAdapter.Holder> {
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_archive, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            final int index = position;
            RegisterBatch batch = batches.get(position);
            String type = batch.getType() == null ? "Запись" : batch.getType();
            if (text(batch.getScreen()).length() > 0) {
                type = type + " · " + batch.getScreen();
            }
            holder.title.setText(type);
            int count = batch.getRegisters() == null ? 0 : batch.getRegisters().size();
            String who = text(batch.getUser());
            String subtitle = (who.length() == 0 ? "" : who + " · ")
                    + batch.getTimestamp() + " · регистров: " + count;
            holder.subtitle.setText(subtitle);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBatch(index);
                }
            });
        }

        @Override
        public int getItemCount() {
            return batches.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final TextView title;
            final TextView subtitle;

            Holder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.archive_title);
                subtitle = (TextView) itemView.findViewById(R.id.archive_subtitle);
            }
        }
    }

    private static class RegisterRows extends RecyclerView.Adapter<RegisterRows.Holder> {
        private final List<Register> registers;

        RegisterRows(List<Register> registers) {
            this.registers = registers == null ? new ArrayList<Register>() : registers;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_row, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Register register = registers.get(position);
            holder.number.setText((register.getNumber() + 1) + ". ");
            holder.title.setText(register.getName() == null ? "" : register.getName());
            holder.value.setText(format(register.getValue()));
            holder.unit.setText(register.getUnit() == null ? "" : register.getUnit());
        }

        @Override
        public int getItemCount() {
            return registers.size();
        }

        private static String format(float value) {
            float rounded = Math.round(value * 100f) / 100f;
            if (Math.abs(rounded - Math.round(rounded)) < 0.001f) {
                return String.valueOf((long) Math.round(rounded));
            }
            return String.format(Locale.US, "%.2f", rounded);
        }

        static class Holder extends RecyclerView.ViewHolder {
            final TextView number;
            final TextView title;
            final TextView value;
            final TextView unit;

            Holder(View itemView) {
                super(itemView);
                number = (TextView) itemView.findViewById(R.id.regNumber);
                title = (TextView) itemView.findViewById(R.id.regTitle);
                value = (TextView) itemView.findViewById(R.id.regValue);
                unit = (TextView) itemView.findViewById(R.id.regUnit);
            }
        }
    }
}
