package com.example.wanderwand.clickImage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wanderwand.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SelectImageActivity extends AppCompatActivity {
    ImageView viewImage;
    Button b, getDetailButton;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    //ProgressDialog progressDoalog;


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
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectImageActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
//                    Uri photoURI = FileProvider.getUriForFile(SelectImageActivity.this, SelectImageActivity.this.getApplicationContext().getPackageName() + ".shareFile", createImageFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    viewImage.setImageBitmap(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    Log.d("pathImage", "onActivityResult: "+path +"\n"+bitmap);

                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                Log.w("selectedImage", "onActivityResult: "+selectedImage.toString());
                String[] filePath = { MediaStore.Images.Media.DATA };
                Log.d("newfile path", "onActivityResult: "+filePath);
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                Log.w("pathImage4", picturePath+"");
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("thumbnail", thumbnail+"");

                editor.putString("SelectImageURL",picturePath);
                editor.apply();
//                SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(SelectImageActivity.this).edit();
//                prefEditor.putString("SelectImageURL", picturePath);
//                prefEditor.apply();
                Log.w("pathImage123", picturePath+"");
                viewImage.setImageBitmap(thumbnail);
            }
        }
    }


}