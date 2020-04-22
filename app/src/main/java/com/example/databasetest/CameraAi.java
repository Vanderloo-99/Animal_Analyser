package com.example.databasetest;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import org.tensorflow.lite.Interpreter;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class CameraAi extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private static final String PICTURE = "com.example.databasetest.MESSAGE";
    ImageView selectedImage;
    Button btnCamera, btnGallery;
    String currentPhotoPath;
    String currentAnimal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_ai);

        selectedImage = findViewById(R.id.displayImageView);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(CameraAi.this, "Camera Button Pressed.", Toast.LENGTH_SHORT).show();
                askCameraPermissions();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(CameraAi.this, "Gallery Button Pressed.", Toast.LENGTH_SHORT).show();
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

    }

    private void askCameraPermissions() {
        //Toast.makeText(CameraAi.this, "Asking Cam and Storage Perms.", Toast.LENGTH_SHORT).show();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*if(requestCode == CAMERA_PERM_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();
            }
        }*/
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), perms[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), perms[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), perms[2]) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(CameraAi.this,perms,CAMERA_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {   //issue
        super.onActivityResult(requestCode, resultCode, data);  //might not be needed
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Absolute URI of Image is: " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag", "OnActivityResult: Gallery Image Uri: " + imageFileName);
                selectedImage.setImageURI(contentUri);
            }
        }
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);  //photo exclusive to app
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);    //display in gallery
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.databasetest.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void testAi(View view){

        ImageView imageView = findViewById(R.id.displayImageView);
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        final Bitmap selectedImage = drawable.getBitmap();
        Bitmap scaledBp =  Bitmap.createScaledBitmap(selectedImage, 100, 100, true);


        ByteBuffer inputBuffer = convertBitmapToByteBuffer(scaledBp);

        float[][] labelArray = new float[1][10];
        try {
            AssetManager assetManager = getAssets();
            String path = "animal10.tflite";

            Interpreter interpreter = new Interpreter(loadModelFile(assetManager,path));
            interpreter.run(inputBuffer, labelArray);
            interpreter.close();
        }
        catch (Exception e){
            Toast.makeText(this, "Error loading model", Toast.LENGTH_SHORT).show();
        }
        String[] topResults = getResult(labelArray);

        TextView first = findViewById(R.id.textView6);
        first.setText(topResults[0]);
        TextView firstProbability = findViewById(R.id.textView7);
        firstProbability.setText(topResults[3]);
        TextView second = findViewById(R.id.textView8);
        second.setText(topResults[1]);
        TextView secondProbability = findViewById(R.id.textView9);
        secondProbability.setText(topResults[4]);
        TextView third = findViewById(R.id.textView10);
        third.setText(topResults[2]);
        TextView thirdProbability = findViewById(R.id.textView11);
        thirdProbability.setText(topResults[5]);

        Button confirm = findViewById(R.id.button6);
        currentAnimal =topResults[0];
        confirm.setVisibility(View.VISIBLE);
    }


    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(4* 1 * 100 * 100 * 3);
        int[] intValues = new int[100*100];
        imgData.order(ByteOrder.nativeOrder());
        imgData.rewind();

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        // Convert the image to floating point.
        int pixel = 0;

        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                final int val = intValues[pixel++];

                imgData.putFloat(((val>> 16) & 0xFF) / 255.f);
                imgData.putFloat(((val>> 8) & 0xFF) / 255.f);
                imgData.putFloat((val & 0xFF) / 255.f);
            }
        }
        return imgData;
    }


    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private String[] getResult(float[][] labelArray){
        float first = 0;
        float second = 0;
        float third = 0;
        int firstAt = 0;
        int secondAt = 0;
        int thirdAt = 0;

        for (int i = 0; i < 10 ; i ++){
            /* If current element is greater than
            first*/
            if (labelArray[0][i] > first)
            {
                third = second;
                thirdAt = secondAt;
                second = first;
                secondAt = firstAt;
                first = labelArray[0][i];
                firstAt = i;

            }
            else if (labelArray[0][i] > second)
            {
                third = second;
                thirdAt = secondAt;
                second = labelArray[0][i];
                secondAt = i;
            }
            else if (labelArray[0][i] > third) {
                third = labelArray[0][i];
                thirdAt = i;
            }
        }


        String[] Animals = {"Butterfly","Cat" ,"Chicken" , "Cow" , "Dog" , "Elephant" , "Horse" , "Sheep", "Spider", "Squirrel"};
        String[] result = {Animals[firstAt],Animals[secondAt],Animals[thirdAt],String.valueOf(first),String.valueOf(second),String.valueOf(third)};
        return result;
    }


    public void addToSighting(View view){
        String animal = currentAnimal;

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        String user = pref.getString("USERNAME", null);
        String UserID = getUserID(user);
        String AnimalID = getAnimalID(animal);
        String date = currentDate();
        SQLiteDatabase mydatabase = openOrCreateDatabase("data",MODE_PRIVATE,null);
        mydatabase.execSQL("INSERT INTO Sightings (UserID, SpeciesID, Date)VALUES('"+ UserID +"','" + AnimalID + "','" + date + "');");

        Intent intent = new Intent(this, Collection.class);
        Toast.makeText(this, animal + " added to your profile", Toast.LENGTH_SHORT).show();
        startActivity(intent);

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
            TextView textView = findViewById(R.id.textView);
            textView.setText("exception user");
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
            TextView textView = findViewById(R.id.textView);
            textView.setText("exception animal");
        }
        return AnimalID;
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


