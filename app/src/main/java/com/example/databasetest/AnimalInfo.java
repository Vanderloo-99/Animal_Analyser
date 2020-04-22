package com.example.databasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AnimalInfo extends AppCompatActivity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_info);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String animal = bundle.getString(Collection.ANIMAL_CHOSEN);
        TextView textView = findViewById(R.id.textView);
        textView.setText(animal);

        String animalPic = animal.toLowerCase() + "2";
        int id = getResources().getIdentifier(animalPic, "drawable", getPackageName());
        Drawable drawable = getResources().getDrawable(id);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageDrawable(drawable);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        String user = pref.getString("USERNAME", null);

        String UserID = getUserID(user);
        String AnimalID= getAnimalID(animal);
        String sightingNo = getSightingNo(UserID, AnimalID);
        TextView sightingNoText = findViewById(R.id.textView2);
        sightingNoText.setText("Number of times seen: " + sightingNo);

        String sightingDate = getSightingDate(UserID, AnimalID);
        TextView sightingDateText = findViewById(R.id.textView3);
        sightingDateText.setText("Last seen: " + sightingDate);

        String averageHeight = getAnimalHeight(AnimalID);
        TextView averageHeightText = findViewById(R.id.textView14);
        averageHeightText.setText("Average Height: " + averageHeight);

        String averageWeight = getAnimalWeight(AnimalID);
        TextView averageWeightText = findViewById(R.id.textView15);
        averageWeightText.setText("Average Weight: " + averageWeight);

        String rarity = getAnimalRarity(AnimalID);
        TextView rarityText = findViewById(R.id.textView5);
        rarityText.setText("Rarity: " + rarity);

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
            Toast.makeText(this, "User database error", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Animal database error", Toast.LENGTH_LONG).show();
        }
        return AnimalID;
    }

    public String getSightingNo(String UserID, String AnimalID){
        String sightingNo = "0";
        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("data", MODE_PRIVATE, null);
            Cursor resultSet = mydatabase.rawQuery("Select * from Sightings where SpeciesID = '" + AnimalID + "' and UserID = '" + UserID + "'", null);
            int count = resultSet.getCount();
            sightingNo = Integer.toString(count);
        }
        catch (Exception e) {
            Toast.makeText(this, "Sighting database error", Toast.LENGTH_LONG).show();
        }
        return sightingNo;
    }

    public String getSightingDate(String UserID, String AnimalID){
        String sightingDate = "";
        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("data", MODE_PRIVATE, null);
            Cursor resultSet = mydatabase.rawQuery("Select Date from Sightings where SpeciesID = '" + AnimalID + "' and UserID = '" + UserID + "' ORDER BY Date DESC", null);
            resultSet.moveToFirst();
            sightingDate = resultSet.getString(0);

        }
        catch (Exception e) {
            Toast.makeText(this, "Animal database error", Toast.LENGTH_LONG).show();
        }
        return sightingDate;
    }

    public String getAnimalWeight(String AnimalID){
        String animalWeight = "";
        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("data", MODE_PRIVATE, null);
            Cursor resultSet = mydatabase.rawQuery("Select AverageWeight from Species where SpeciesID = '" + AnimalID + "'", null);
            resultSet.moveToFirst();
            animalWeight = resultSet.getString(0);

        }
        catch (Exception e) {
            Toast.makeText(this, "Animal database error", Toast.LENGTH_LONG).show();
        }
        return animalWeight;
    }
    public String getAnimalRarity(String AnimalID){
        String rarity = "";
        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("data", MODE_PRIVATE, null);
            Cursor resultSet = mydatabase.rawQuery("Select Rarity from Species where SpeciesID = '" + AnimalID + "'", null);
            resultSet.moveToFirst();
            rarity = resultSet.getString(0);

        }
        catch (Exception e) {
            Toast.makeText(this, "Animal database error", Toast.LENGTH_LONG).show();
        }
        return rarity;
    }

    public String getAnimalHeight(String AnimalID){
        String animalHeight = "";
        try {
            SQLiteDatabase mydatabase = openOrCreateDatabase("data", MODE_PRIVATE, null);
            Cursor resultSet = mydatabase.rawQuery("Select AverageHeight from Species where SpeciesID = '" + AnimalID + "'", null);
            resultSet.moveToFirst();
            animalHeight = resultSet.getString(0);

        }
        catch (Exception e) {
            Toast.makeText(this, "Animal database error", Toast.LENGTH_LONG).show();
        }
        return animalHeight;
    }
}
