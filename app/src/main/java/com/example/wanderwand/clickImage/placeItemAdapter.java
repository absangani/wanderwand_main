package com.example.wanderwand.clickImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanderwand.R;

import java.util.ArrayList;

import utils.PleceDetailsModel;

public class placeItemAdapter extends RecyclerView.Adapter<placeItemAdapter.Viewholder> {

    private Context context;
    private ArrayList<PleceDetailsModel> PleceDetailsModelArrayList;

    private OnItemClickListener onClick;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Constructor
    public placeItemAdapter(Context context, ArrayList<PleceDetailsModel> PleceDetailsModelArrayList) {
        this.context = context;
        this.PleceDetailsModelArrayList = PleceDetailsModelArrayList;
    }

    @NonNull
    @Override
    public placeItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        PleceDetailsModel model = PleceDetailsModelArrayList.get(position);
        holder.PleceNameTV.setText(model.getPlece_name());
        holder.PleceRatingTV.setText("" + model.getPlece_rating());
        holder.PleceIV.setImageResource(model.getPlece_image());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return PleceDetailsModelArrayList.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView PleceIV;
        private TextView PleceNameTV, PleceRatingTV;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            PleceIV = itemView.findViewById(R.id.idIVPleceImage);
            PleceNameTV = itemView.findViewById(R.id.idTVPleceName);
            PleceRatingTV = itemView.findViewById(R.id.idTVPleceRating);
        }
    }
}
