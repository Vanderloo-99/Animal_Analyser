package com.example.databasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class Collection extends AppCompatActivity {

    public static final String ANIMAL_CHOSEN = "com.example.databasetest.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);


        for(int i = 1; i <= 10; i++) {
            String buttonID = "animal" + i;
            testLocks(buttonID);
        }

    }
    public void onCameraPress(View view){
        Intent intent = new Intent(this, CameraAi.class);
        startActivity(intent);
    }

    public void onAnimalPress(View view){
        String admin = isAdmin();
        if((String)view.getTag(R.id.lock) != "locked") {
            Intent intent = new Intent(this, AnimalInfo.class);
            String message = (String) view.getTag();
            intent.putExtra(ANIMAL_CHOSEN, message);
            startActivity(intent);
        }
        else if(admin.equals("true")) {
            unlockAnimal((String)view.getTag());
        }
    }

    private void unlockAnimal(String animal){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        String user = pref.getString("USERNAME", null);

        String UserID = getUserID(user);
        String AnimalID = getAnimalID(animal);
        String date = currentDate();

        SQLiteDatabase mydatabase = openOrCreateDatabase("data",MODE_PRIVATE,null);
        mydatabase.execSQL("INSERT INTO Sightings (UserID, SpeciesID, Date)VALUES('"+ UserID +"','" + AnimalID + "','" + date + "');");

        Toast.makeText(Collection.this, animal + " unlocked", Toast.LENGTH_LONG).show();

        for(int i = 1; i <= 10; i++) {
            String buttonID = "animal" + i;
            testLocks(buttonID);
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

    private String isAdmin(){
        String admin = "false";

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        String user = pref.getString("USERNAME", null);
        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("data",MODE_PRIVATE,null);
            Cursor resultSet = mydatabase.rawQuery("Select Admin from User where Username = '" + user + "'",null);
            resultSet.moveToFirst();
            admin = resultSet.getString(0);

        }
        catch (Exception e) {
            Toast.makeText(Collection.this, "User database error", Toast.LENGTH_SHORT).show();

        }
        return admin;
    }

    public void onLogoutPress(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void testLocks(String buttonID){
        int count = 0;
        int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
        ImageButton button = ((ImageButton)findViewById(resID));
        String animal = (String)button.getTag();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        String user = pref.getString("USERNAME", null);

        String AnimalID = getAnimalID(animal);
        String UserID = getUserID(user);

        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("data", MODE_PRIVATE, null);
            Cursor resultSet = mydatabase.rawQuery("Select * from Sightings where SpeciesID = '" + AnimalID + "' and UserID = '" + UserID + "'", null);
            count = resultSet.getCount();

        }
        catch(Exception e){

        }
        if(count == 0){
            int id = getResources().getIdentifier("lock2", "drawable", getPackageName());
            Drawable drawable = getResources().getDrawable(id);
            button.setImageDrawable(drawable);
            button.setTag(R.id.lock,"locked");
        }
        else {
            String animalLower = animal.toLowerCase();
            int id = getResources().getIdentifier(animalLower + "2", "drawable", getPackageName());
            Drawable drawable = getResources().getDrawable(id);
            button.setImageDrawable(drawable);
            button.setTag(R.id.lock,"unlocked");
        }
    }

    public String getUserID(String user){
        String UserID = "0";
        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("data",MODE_PRIVATE,null);
            Cursor resultSet = mydatabase.rawQuery("Select * from User where Username = '" + user + "'",null);
            resultSet.moveToFirst();
            String ID = resultSet.getString(0);
            UserID = ID;
        }
        catch (Exception e) {
            Toast.makeText(Collection.this, "User database error", Toast.LENGTH_LONG).show();

        }
        return UserID;
    }

    public String getAnimalID(String animal){
        String AnimalID = "0";
        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("data",MODE_PRIVATE,null);
            Cursor resultSet = mydatabase.rawQuery("Select * from Species where SpeciesName = '" + animal + "'",null);
            resultSet.moveToFirst();
            String ID = resultSet.getString(0);
            AnimalID = ID;
        }
        catch (Exception e) {
            Toast.makeText(Collection.this, "Animal database error", Toast.LENGTH_LONG).show();
        }
        return AnimalID;
    }




}
