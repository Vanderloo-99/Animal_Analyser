package com.example.databasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase mydatabase = openOrCreateDatabase("data",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Species (SpeciesID INTEGER PRIMARY KEY AUTOINCREMENT, SpeciesName VARCHAR UNIQUE, AverageWeight VARCHAR, AverageHeight VARCHAR, Rarity VARCHAR );");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Sightings (SightingID INTEGER PRIMARY KEY AUTOINCREMENT, UserID INTEGER REFERENCES User (UserID), SpeciesID INTEGER REFERENCES Species (SpeciesID), Date DATETIME);");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS User (UserID INTEGER PRIMARY KEY AUTOINCREMENT, Username VARCHAR UNIQUE, Password VARCHAR, DisplayName VARCHAR, DateCreated DATETIME, Admin VARCHAR);");
        int count = 0;
        try {
            Cursor resultSet = mydatabase.rawQuery("Select * from Species", null);
            count = resultSet.getCount();
            TextView textView = findViewById(R.id.textView);
            textView.setText(Integer.toString(count));

        }
        catch(Exception e){

        }
        if(count == 0){
            mydatabase.execSQL("INSERT INTO Species (SpeciesID, SpeciesName, AverageWeight, AverageHeight, Rarity)VALUES('1','Butterfly','0 - 3 g','0.5 - 30 cm','Epic');");
            mydatabase.execSQL("INSERT INTO Species (SpeciesName, AverageWeight, AverageHeight, Rarity)VALUES('Cat','3.6 – 4.5 kg','23 – 25 cm','Common');");
            mydatabase.execSQL("INSERT INTO Species (SpeciesName, AverageWeight, AverageHeight, Rarity)VALUES('Cow','720 - 1100 kg','1.4 - 2.6m','Rare');");
            mydatabase.execSQL("INSERT INTO Species (SpeciesName, AverageWeight, AverageHeight, Rarity)VALUES('Dog','12 - 30 kg','15 – 110 cm','Common');");
            mydatabase.execSQL("INSERT INTO Species (SpeciesName, AverageWeight, AverageHeight, Rarity)VALUES('Elephant','2700 - 6000 kg','2.7 - 3.2 m','Legendary');");
            mydatabase.execSQL("INSERT INTO Species (SpeciesName, AverageWeight, AverageHeight, Rarity)VALUES('Horse','380 - 1000 kg','1.4 – 1.8 m','Rare');");
            mydatabase.execSQL("INSERT INTO Species (SpeciesName, AverageWeight, AverageHeight, Rarity)VALUES('Spider','0.05 – 170 g','0.5 - 7 cm','Epic');");
            mydatabase.execSQL("INSERT INTO Species (SpeciesName, AverageWeight, AverageHeight, Rarity)VALUES('Squirrel','400 - 600 g','23 - 30 cm','Epic');");
            mydatabase.execSQL("INSERT INTO Species (SpeciesName, AverageWeight, AverageHeight, Rarity)VALUES('Sheep','45 - 160 kg','85 - 140 cm','Rare');");
            mydatabase.execSQL("INSERT INTO Species (SpeciesName, AverageWeight, AverageHeight, Rarity)VALUES('Chicken','1.8 - 4.5 kg','30 - 60 cm','Rare');");
            mydatabase.execSQL("INSERT INTO User (UserID, Username, Password, DisplayName, DateCreated, Admin)VALUES('1','Test','abc','Tester','15-04-20','false');");
            mydatabase.execSQL("INSERT INTO User (Username, Password, DisplayName, DateCreated, Admin)VALUES('mpage','1234','Matt','15-04-20','true');");
            mydatabase.execSQL("INSERT INTO Sightings (SightingID, UserID, SpeciesID, Date)VALUES('1','1','4','16-04-20');");
        }


    }

    public void Register(View view){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void Login(View view){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }


}
