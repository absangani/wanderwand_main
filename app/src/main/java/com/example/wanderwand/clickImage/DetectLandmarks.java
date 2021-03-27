package com.example.wanderwand.clickImage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wanderwand.R;
import com.google.android.material.textview.MaterialTextView;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.LocationInfo;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetectLandmarks extends AppCompatActivity {

    MaterialTextView textView;
    SharedPreferences preferences;
    String selectedImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_landmarks);
        textView = findViewById(R.id.newtextview);
        String jsonPath = "file:///android_asset/original-seeker-307507-22d185f604b6.json ";
        try {
            authExplicit(jsonPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectImage();

//        authImplicit();

    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
        startActivityForResult(chooser, 101);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case 100:
//                    Uri galleryImageUri = data.getData();
//                    try {
//                        Log.e("Image Path Gallery", getPath(getActivity(), galleryImageUri));
//                        selectedImagePath = getPath(getActivity(), galleryImageUri);
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        Log.e("Image Path Gallery", galleryImageUri.getPath());
//                        selectedImagePath = galleryImageUri.getPath();
//                    }

                    break;

                case 101:
                    // Uri cameraImageUri = initialURI;
                    Uri cameraImageUri = data.getData();
                    Log.e("Image Path Camera", getPath(cameraImageUri));
                    selectedImagePath = getPath(cameraImageUri);
                    try {
                        detectLandmarks();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = DetectLandmarks.this.managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public void detectLandmarks() throws IOException {
    // TODO(developer): Replace these variables before running the sample.
//        Uri fileUri = Uri.parse("android.resource://your_packagename/" + R.drawable.hotel);
//        String imageUrl = preferences.getString("SelectImageURL", null);
        String filePath = selectedImagePath;
        detectLandmarks(filePath);
    }

    static void authExplicit(String jsonPath) throws IOException {
  // You can specify a credential file by providing a path to GoogleCredentials.
  // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
  GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
        .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
  Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

  System.out.println("Buckets:");
  Page<Bucket> buckets = storage.list();
  for (Bucket bucket : buckets.iterateAll()) {
    System.out.println(bucket.toString());
  }
}

    // Detects landmarks in the specified local image.
    public static void detectLandmarks(String filePath) throws IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LANDMARK_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getLandmarkAnnotationsList()) {
                    LocationInfo info = annotation.getLocationsList().listIterator().next();
                    System.out.format("Landmark: %s%n %s%n", annotation.getDescription(), info.getLatLng());
                }
            }
        }
    }

}