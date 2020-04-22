package com.example.databasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    public static final String USER_NAME = "com.example.databasetest.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onbuttonpress(View view) {
        EditText userEdit = (EditText) findViewById(R.id.user);
        String user = userEdit.getText().toString();
        EditText passEdit = (EditText) findViewById(R.id.pass);
        String pass = passEdit.getText().toString();

        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("data",MODE_PRIVATE,null);
            Cursor resultSet = mydatabase.rawQuery("Select Username, Password, DisplayName from User where Username = '" + user + "'",null);
            resultSet.moveToFirst();
            String username = resultSet.getString(0);
            String password = resultSet.getString(1);
            String display = resultSet.getString(2);

            if(pass.equals(password)){
                Toast.makeText(Login.this, "Logged in as " + display, Toast.LENGTH_SHORT).show();

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.putString("USERNAME", username); // Storing string
                editor.commit();

                Intent intent = new Intent(this, Collection.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(Login.this, "Your password is incorrect", Toast.LENGTH_LONG).show();

            }


        }
        catch (Exception e) {
            Toast.makeText(Login.this, "Your username is incorrect", Toast.LENGTH_LONG).show();
        }
    }
}
