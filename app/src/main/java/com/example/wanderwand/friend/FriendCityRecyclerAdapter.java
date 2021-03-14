package com.example.wanderwand.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanderwand.R;

import java.util.ArrayList;

import objects.FriendCity;

public class FriendCityRecyclerAdapter extends RecyclerView.Adapter<FriendCityRecyclerAdapter.FriendCityViewHolder> {

    private ArrayList<FriendCity> mCityList;
    private Context mContext;

    public FriendCityRecyclerAdapter(Context context, ArrayList<FriendCity> list) {
        mCityList = list;
        mContext = context;
    }

    @NonNull
    @Override
    public FriendCityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendCityViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.friend_city_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendCityViewHolder holder, int position) {
        holder.friendCityTextView.setText(mCityList.get(position).getCityName());
    }

    @Override
    public int getItemCount() {
        return mCityList.size();
    }

    public class FriendCityViewHolder extends RecyclerView.ViewHolder {
        public TextView friendCityTextView;

        public FriendCityViewHolder(View itemView) {
            super(itemView);
            friendCityTextView = itemView.findViewById(R.id.city_name_text_view);
        }
    }

}
