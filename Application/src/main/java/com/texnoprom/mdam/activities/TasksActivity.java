package com.texnoprom.mdam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.models.FieldTask;
import com.texnoprom.mdam.services.TaskStore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksActivity extends AppCompatActivity {

    private RecyclerView list;
    private TextView empty;
    private String owner = "";
    private List<FieldTask> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        android.support.v7.widget.Toolbar toolbar =
                (android.support.v7.widget.Toolbar) findViewById(R.id.tasksToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        owner = PreferenceManager.getDefaultSharedPreferences(this).getString("userName", "");
        if (owner == null) {
            owner = "";
        }
        setTitle(owner.length() == 0 || "Not Available".equals(owner)
                ? "Мои задачи"
                : "Мои задачи: " + owner);

        list = (RecyclerView) findViewById(R.id.tasks_list);
        empty = (TextView) findViewById(R.id.tasks_empty);
        list.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.task_add) {
            showAddDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTasks() {
        tasks = TaskStore.load(this, owner);
        Collections.sort(tasks, new Comparator<FieldTask>() {
            @Override
            public int compare(FieldTask left, FieldTask right) {
                if (left.isDone() != right.isDone()) {
                    return left.isDone() ? 1 : -1;
                }
                return Long.compare(right.getCreatedAt(), left.getCreatedAt());
            }
        });
        empty.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
        list.setVisibility(tasks.isEmpty() ? View.GONE : View.VISIBLE);
        list.setAdapter(new TaskAdapter());
    }

    private void showAddDialog() {
        final EditText name = new EditText(this);
        name.setHint("Объект, например КП-12");
        name.setSingleLine(true);
        final EditText note = new EditText(this);
        note.setHint("Что сделать");
        note.setSingleLine(true);
        final Spinner devices = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TaskStore.DEVICES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        devices.setAdapter(adapter);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        box.setPadding(pad, pad, pad, 0);
        box.addView(name);
        box.addView(devices);
        box.addView(note);

        new AlertDialog.Builder(this)
                .setTitle("Новая задача")
                .setView(box)
                .setPositiveButton("Сохранить", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        String device = (String) devices.getSelectedItem();
                        FieldTask created = TaskStore.add(
                                TasksActivity.this, owner, name.getText().toString(), device,
                                note.getText().toString());
                        if (created == null) {
                            Toast.makeText(TasksActivity.this, "Введите имя объекта", Toast.LENGTH_LONG).show();
                            return;
                        }
                        showTasks();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void openTask(FieldTask task) {
        Class<?> screen = screenFor(task.getDeviceType());
        if (screen == null) {
            Toast.makeText(this, "Неизвестный модуль", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, screen);
        intent.putExtra("deviceType", task.getDeviceType());
        intent.putExtra("taskId", task.getId());
        startActivity(intent);
    }

    private void showTaskMenu(final FieldTask task) {
        String mark = task.isDone() ? "Вернуть в работу" : "Отметить выполненной";
        new AlertDialog.Builder(this)
                .setTitle(task.getObjectName())
                .setItems(new CharSequence[]{"Открыть модуль", mark, "Удалить"},
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                if (which == 0) {
                                    openTask(task);
                                } else if (which == 1) {
                                    if (task.isDone()) {
                                        TaskStore.reopen(TasksActivity.this, task.getId());
                                    } else {
                                        TaskStore.complete(TasksActivity.this, task.getId());
                                    }
                                    showTasks();
                                } else {
                                    TaskStore.remove(TasksActivity.this, task.getId());
                                    showTasks();
                                }
                            }
                        })
                .show();
    }

    private static Class<?> screenFor(String device) {
        if ("БКМ1".equals(device) || "БКМ4".equals(device)) {
            return BTActivity.class;
        }
        if ("ММПР".equals(device) || "ОПЕ11".equals(device) || "ОПЕ14".equals(device)) {
            return TCPActivity.class;
        }
        return null;
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.Holder> {
        private final SimpleDateFormat format =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_archive, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            final FieldTask task = tasks.get(position);
            holder.title.setText(task.getObjectName());
            String when = format.format(new Date(task.getCreatedAt()));
            String status = task.isDone() ? "Выполнена" : "Новая";
            String line = task.getDeviceType() + " · " + status + " · " + when;
            if (task.getNote() != null && task.getNote().trim().length() > 0) {
                line = line + "\n" + task.getNote().trim();
            }
            holder.subtitle.setText(line);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTask(task);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showTaskMenu(task);
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return tasks.size();
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
}
