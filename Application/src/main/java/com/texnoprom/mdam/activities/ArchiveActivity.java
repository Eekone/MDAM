package com.texnoprom.mdam.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.models.Register;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.services.ApiHelper;
import com.texnoprom.mdam.services.LocalArchive;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArchiveActivity extends AppCompatActivity {

    private RecyclerView list;
    private TextView empty;
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
        setTitle(type);
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
            holder.title.setText(type);
            int count = batch.getRegisters() == null ? 0 : batch.getRegisters().size();
            holder.subtitle.setText(batch.getTimestamp() + " · регистров: " + count);
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
