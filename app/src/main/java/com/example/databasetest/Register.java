package com.example.databasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onbuttonpress(View view) {
        EditText userEdit = (EditText) findViewById(R.id.user);
        String user = userEdit.getText().toString();
        EditText passEdit = (EditText) findViewById(R.id.pass);
        String pass = passEdit.getText().toString();
        EditText displayEdit = (EditText) findViewById(R.id.display);
        String display = displayEdit.getText().toString();
        String date = currentDate();
        CheckBox adminCheck = findViewById(R.id.admin);
        String admin = "false";
        if(adminCheck.isChecked()){
            admin = "true";
        }


        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("data",MODE_PRIVATE,null);
            mydatabase.execSQL("INSERT INTO User (Username, Password, DisplayName, DateCreated, Admin)VALUES('" + user + "', '" + pass + "', '" + display + "', '" + date + "', '"+ admin +"');");


            Intent intent = new Intent(this, MainActivity.class);
            Toast.makeText(Register.this, display + " registered", Toast.LENGTH_SHORT).show();
            startActivity(intent);

        }
        catch (Exception e) {
            Toast.makeText(Register.this, "Please register with different credentials", Toast.LENGTH_LONG).show();
        }


    }

    public static String currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        // get current date time with Date()
        Date date = new Date();
        // System.out.println(dateFormat.format(date));
        // don't print it, but save it!
        return dateFormat.format(date);
    }
}
