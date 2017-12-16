package com.texnoprom.mdam.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.texnoprom.mdam.R;

import java.util.ArrayList;
import java.util.List;

public class AdministrationActivity extends AppCompatActivity {

    private Spinner spinner;
    private AccountManager accountManager;
    private EditText userNameText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administration);
        Toolbar t = (Toolbar) findViewById(R.id.administrationToolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accountManager = AccountManager.get(this);

        spinner = (Spinner) findViewById(R.id.delUserSpinner);
        setSpinnerAdapter();
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
        userNameText = (EditText) findViewById(R.id.addName);
        passwordText = (EditText) findViewById(R.id.addPin);

        Button addUser = (Button) findViewById(R.id.addUserButton);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser(userNameText.getText().toString(), passwordText.getText().toString());
            }
        });

        Button delUser = (Button) findViewById(R.id.delUserButton);
        delUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delUser(spinner.getSelectedItem().toString());
            }
        });
        hideKeyboard();
    }

    private void addUser(String userName, String password) {
        View focusView;
        if (userName.equals("")) {
            userNameText.setError(getString(R.string.error_needed));
            focusView = userNameText;
            focusView.requestFocus();
            return;
        }
        if (password.equals("")) {
            passwordText.setError(getString(R.string.error_needed));
            focusView = passwordText;
            focusView.requestFocus();
            return;
        }

        Account account = new Account(userName, "BCMUUser");
        Account[] accounts = accountManager.getAccountsByType("BCMUUser");
        for (Account acc : accounts) {
            if (acc.name.equals(account.name)) {
                userNameText.setError(getString(R.string.error_same_user));
                focusView = userNameText;
                focusView.requestFocus();
                return;
            }
        }
        accountManager.addAccountExplicitly(account, password, null);
        setSpinnerAdapter();
        passwordText.setText("");
        passwordText.clearFocus();
        hideKeyboard();
        userNameText.setText("");
        Toast.makeText(this, "Пользователь добавлен",
                Toast.LENGTH_LONG).show();

    }

    private void delUser(String userToDel) {
        Account[] accounts = accountManager.getAccountsByType("BCMUUser");
        Account accToDel = null;
        for (Account account : accounts) {
            if (account.name.equals(userToDel)) {
                accToDel = account;
            }
        }
        accountManager.removeAccountExplicitly(accToDel);
        setSpinnerAdapter();
    }

    private void setSpinnerAdapter() {
        List<String> names = new ArrayList<>();
        Account[] accounts = accountManager.getAccountsByType("BCMUUser");
        for (Account account : accounts) {
            names.add(account.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                getCurrentFocus().getWindowToken(), 0);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getWindowToken(), 0);
    }
}
