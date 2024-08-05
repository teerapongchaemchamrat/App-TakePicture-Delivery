package com.example.btc_delivery_v2;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Button btn_photo1, btn_photo2, btn_photo3, btn_save, btn_qr;
    TextView txt_co, txt_line;
    EditText txt_qty;
    //String txt_original;
    ImageView image1, image2, image3;
    private String currentPhotoPath1, currentPhotoPath2, currentPhotoPath3;
    private RetrofitAPI retrofitAPI;
    private MultipartBody.Part filePart1;
    private MultipartBody.Part filePart2;
    private MultipartBody.Part filePart3;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //txt_bill = findViewById(R.id.txt_bill);
        txt_co = findViewById(R.id.txt_co);
        txt_line = findViewById(R.id.txt_line);
        txt_qty = findViewById(R.id.txt_qty);

        image1 = findViewById(R.id.image_photo1);
        image2 = findViewById(R.id.image_photo2);
        image3 = findViewById(R.id.image_photo3);
        btn_qr = findViewById(R.id.btn_scanQR);
        btn_photo1 = findViewById(R.id.btn_photo1);
        btn_photo2 = findViewById(R.id.btn_photo2);
        btn_photo3 = findViewById(R.id.btn_photo3);
        btn_save = findViewById(R.id.btn_save);

        btn_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartScaning();
            }
        });

        btn_photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, 100);
                }

                dispatchTakePictureIntent1();
            }
        });

        btn_photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, 200);
                }

                dispatchTakePictureIntent2();
            }

        });

        btn_photo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, 300);
                }

                dispatchTakePictureIntent3();
            }
        });

        initializeRetrofit();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txt_co.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter Bill Number" , Toast.LENGTH_LONG).show();
                    return;
                }
                if(currentPhotoPath1 == null){
                    Toast.makeText(MainActivity.this, "Photo1 not image" , Toast.LENGTH_LONG).show();
                    return;
                }

                BitmapDrawable drawable1 = (BitmapDrawable) image1.getDrawable();
                Bitmap bitmap1 = drawable1.getBitmap();

                if (bitmap1 == null) {
                    Toast.makeText(MainActivity.this, "Photo1 not image" , Toast.LENGTH_LONG).show();
                    return;
                }

                File imageFile1 = new File(currentPhotoPath1);
                File imageFile2 = null;
                File imageFile3 = null;

                if(currentPhotoPath2 != null){
                    imageFile2 = new File(currentPhotoPath2);
                }

                if(currentPhotoPath3 != null){
                    imageFile3 = new File(currentPhotoPath3);
                }
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                uploadImage(imageFile1, imageFile2, imageFile3);
            }
        });
    }

    private void StartScaning(){
        Intent intent = new Intent(MainActivity.this, Zxing_scanner.class);
        startActivityForResult(intent,400);

    }

    private void dispatchTakePictureIntent1() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to store the image
            File photoFile1 = null;
            try {
                photoFile1 = createImageFile1();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile1 != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.btc_delivery_v2.fileprovider", photoFile1);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 100);
            }
        }
    }

    private void dispatchTakePictureIntent2() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to store the image
            File photoFile2 = null;
            try {
                photoFile2 = createImageFile2();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile2 != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.btc_delivery_v2.fileprovider", photoFile2);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 200);
            }
        }
    }

    private void dispatchTakePictureIntent3() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to store the image
            File photoFile3 = null;
            try {
                photoFile3 = createImageFile3();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile3 != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.btc_delivery_v2.fileprovider", photoFile3);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 300);
            }
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("QR Code Request Code", "Request Code: " + requestCode);
        if(requestCode == 100){
            try {
                Bitmap bitmap1 = BitmapFactory.decodeFile(currentPhotoPath1);
                image1.setImageBitmap(bitmap1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(requestCode == 200){
            try {
                Bitmap bitmap2 = BitmapFactory.decodeFile(currentPhotoPath2);
                image2.setImageBitmap(bitmap2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(requestCode == 300){
            try {
                Bitmap bitmap3 = BitmapFactory.decodeFile(currentPhotoPath3);
                image3.setImageBitmap(bitmap3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(requestCode == 400){
            try {
                if(resultCode == RESULT_OK){
                    String strText0 = data.getStringExtra("co_num");
                    String strText1 = data.getStringExtra("co_line");
                    txt_co.setText(strText0);
                    txt_line.setText(strText1);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private File createImageFile1() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile1 = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath1 = imageFile1.getAbsolutePath();

        return imageFile1;
    }
    private File createImageFile2() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile2 = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath2 = imageFile2.getAbsolutePath();

        return imageFile2;
    }
    private File createImageFile3() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile3 = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath3 = imageFile3.getAbsolutePath();

        return imageFile3;
    }

    private void initializeRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://XX.XX.XX.XX:XX/") // Replace with your API endpoint URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    private void uploadImage(File imageFile1, File imageFile2, File imageFile3) {

        if (imageFile1 == null) {
            Toast.makeText(MainActivity.this, "upload image1 is value NULL" , Toast.LENGTH_LONG).show();
            return;
        }

        RequestBody requestBody1 = RequestBody.create(MediaType.parse("image1/jpeg"), imageFile1);
        filePart1 = MultipartBody.Part.createFormData("file1", imageFile1.getName(), requestBody1);
        filePart2 = null;
        filePart3 = null;

        if (imageFile2 != null) {
            RequestBody requestBody2 = RequestBody.create(MediaType.parse("image2/jpeg"), imageFile2);
            filePart2 = MultipartBody.Part.createFormData("file2", imageFile2.getName(), requestBody2);
        }

        if (imageFile3 != null) {
            RequestBody requestBody3 = RequestBody.create(MediaType.parse("image3/jpeg"), imageFile3);
           filePart3 = MultipartBody.Part.createFormData("file3", imageFile3.getName(), requestBody3);
        }

        RequestBody co_num_RequestBody = RequestBody.create(MediaType.parse("text/plain"), txt_co.getText().toString());
        RequestBody co_line_RequestBody = RequestBody.create(MediaType.parse("text/plain"), txt_line.getText().toString());
        RequestBody qty_RequestBody = RequestBody.create(MediaType.parse("text/plain"), txt_qty.getText().toString());

        Call<ResponseBody> call = retrofitAPI.uploadPicture(filePart1, filePart2, filePart3, co_num_RequestBody,co_line_RequestBody, qty_RequestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Image uploaded successfully
                    Toast.makeText(MainActivity.this, "บันทึกข้อมูลสำเร็จ", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Image uploaded successfully");

                    txt_co.setText("");
                    txt_line.setText("");
                    txt_qty.setText("");
                    currentPhotoPath1 = null;
                    currentPhotoPath2 = null;
                    currentPhotoPath3 = null;

                    image1.setImageBitmap(null);
                    image2.setImageBitmap(null);
                    image3.setImageBitmap(null);

                    filePart1 = null;
                    filePart2 = null;
                    filePart3 = null;

                    progressDialog.dismiss();

                } else {
                    // Handle API error
                    Toast.makeText(MainActivity.this, "API error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "API error: " + response.message());
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network or other errors
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Network error: " + t.getMessage());
                progressDialog.dismiss();
            }
        });
    }
}
