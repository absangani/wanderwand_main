package com.example.wanderwand.clickImage;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.wanderwand.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SelectImageActivity extends AppCompatActivity {
    ImageView viewImage;
    Button b, getDetailButton;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    //ProgressDialog progressDoalog;

    private Uri imageUri;
    private String imageUrl;

    final static int Gallery_Pick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        b=(Button)findViewById(R.id.btnSelectPhoto);
        viewImage=(ImageView)findViewById(R.id.viewImage);
        getDetailButton = findViewById(R.id.btnGetDetails);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        getDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageDetails();
            }
        });
    }

    private void getImageDetails() {
//        boolean isSelected = preferences.contains("SelectImageURL");
        boolean isSelected = true;
        if(isSelected) {

            Intent intent = new Intent(SelectImageActivity.this, ListOfPlacesActivity.class);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(SelectImageActivity.this,"please select image",Toast.LENGTH_SHORT).show();
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds options to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    private void selectImage() {
        final CharSequence[] items = { "Camera","Gallery", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectImageActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (items[i].equals("Camera")) {
                    if(hasPermission(SelectImageActivity.this, Manifest.permission.CAMERA)){
                        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(camera,1000);
                    }else {
                        // ask for camera permissions
                        ActivityCompat.requestPermissions(SelectImageActivity.this,new String[]{Manifest.permission.CAMERA},102);
                    }
                } else if (items[i].equals("Gallery")) {
                    if(hasPermission(SelectImageActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent, Gallery_Pick);
                    }else {
                        ActivityCompat.requestPermissions(SelectImageActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},103);
                    }

                } else if (items[i].equals("Cancel")) {
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 102){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                // permission is granted
                Toast.makeText(this, "Permission is Granted.", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permission is Denied.", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == 103){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                // permission is granted
                Toast.makeText(this, "Permission is Granted.", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(this, "Permission is Denied.", Toast.LENGTH_SHORT).show();
            }
        }
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            // alert dialog

            AlertDialog.Builder extraInfo = new AlertDialog.Builder(this);
            extraInfo.setTitle("Storage Permission is Required.");
            extraInfo.setMessage("To Run this app, App needs access to storage to save the file.");

            extraInfo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(SelectImageActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},103);
                }
            });

            extraInfo.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(SelectImageActivity.this, "Some Feature of App Might not Work.", Toast.LENGTH_SHORT).show();
                }
            });
            extraInfo.create().show();
        }
    }

    private boolean hasPermission(Context context, String permission){
        return ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== Gallery_Pick && resultCode==RESULT_OK && data!=null){
            imageUri= data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        else if (requestCode==1000 && resultCode==RESULT_OK && data!=null){
            imageUri= data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                   /* Uri resultUri = result.getUri();

                    // Firebase Storage
                    /final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                    filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downUri = task.getResult();

                                final String downloadUrl = downUri.toString();

                                //Database
                                UsersRef.child("profileimage").setValue(downloadUrl).
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(SelectImageActivity.this, "Profile Image stored to Database Successfully...", Toast.LENGTH_SHORT).show();

                                                }else{
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(SelectImageActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });*/

            }else{
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}