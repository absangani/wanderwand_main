package com.example.wanderwand.clickImage;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanderwand.R;

import java.util.ArrayList;

import utils.PleceDetailsModel;

public class ListOfPlacesActivity extends AppCompatActivity {

    private RecyclerView PleceRV;
    ProgressDialog progressDoalog;

    // Arraylist for storing data
    private ArrayList<PleceDetailsModel> PleceDetailsModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_places);
        showProgress();
        new callAdepter().execute();

    }

    private void showProgress() {
        Handler handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progressDoalog.incrementProgressBy(1);
            }
        };
        progressDoalog = new ProgressDialog(ListOfPlacesActivity.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Image Scaning....");
        progressDoalog.setTitle("Get Image Details");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDoalog.getWindow().getDecorView().getBackground().setColorFilter(new LightingColorFilter(0xFF000000, 0xFF303030));
        progressDoalog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (progressDoalog.getProgress() <= progressDoalog
                            .getMax()) {
                        Thread.sleep(200);
                        handle.sendMessage(handle.obtainMessage());
                        if (progressDoalog.getProgress() == progressDoalog
                                .getMax()) {
                            progressDoalog.dismiss();

                        }
                    }
                    Log.d("startcativety", "run: activity started");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();

    }

    public class callAdepter extends AsyncTask<Void, Void, Void> {
        @Override
        public void onPreExecute() {
            PleceRV = findViewById(R.id.PlaceView);
            // here we have created new array list and added data to it.
            PleceDetailsModelArrayList = new ArrayList<>();
            PleceDetailsModelArrayList.add(new PleceDetailsModel("Tajmahel", 4, R.drawable.hotel));
            PleceDetailsModelArrayList.add(new PleceDetailsModel("Kutub Minar", 3, R.drawable.ic_home));
            PleceDetailsModelArrayList.add(new PleceDetailsModel("Goa Bich", 4, R.drawable.ic_home));
//            PleceDetailsModelArrayList.add(new PleceDetailsModel("DSA in C++", 4, R.drawable.ic_home));
//            PleceDetailsModelArrayList.add(new PleceDetailsModel("Kotlin for Android", 4, R.drawable.ic_home));
//            PleceDetailsModelArrayList.add(new PleceDetailsModel("Java for Android", 4, R.drawable.ic_home));
//            PleceDetailsModelArrayList.add(new PleceDetailsModel("HTML and CSS", 4, R.drawable.ic_home));

            // we are initializing our adapter class and passing our arraylist to it.
            placeItemAdapter placeItemAdapter = new placeItemAdapter(ListOfPlacesActivity.this, PleceDetailsModelArrayList);

            // below line is for setting a layout manager for our recycler view.
            // here we are creating vertical list so we will provide orientation as vertical
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListOfPlacesActivity.this, LinearLayoutManager.VERTICAL, false);

            // in below two lines we are setting layoutmanager and adapter to our recycler view.
            PleceRV.setLayoutManager(linearLayoutManager);
            PleceRV.setAdapter(placeItemAdapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}