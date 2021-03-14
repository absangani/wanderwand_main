package com.example.wanderwand.friend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wanderwand.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import objects.User;


class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.MyFriendsViewHolder> {

    private final Context mContext;
    private final List<User> mFriends;
    private LayoutInflater mInflater;

    MyFriendsAdapter(Context context, List<User> friends) {
        this.mContext = context;
        this.mFriends = friends;
        mInflater = (LayoutInflater) mContext.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public MyFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.friend_listitem, parent, false);
        return new MyFriendsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyFriendsViewHolder holder, int position) {

        Picasso.get().load(mFriends.get(position).getImage()).placeholder(R.drawable.default_user_icon)
                .error(R.drawable.default_user_icon)
                .into(holder.friendImage);
        String fullName = mFriends.get(position).getFirstName() + " " + mFriends.get(position).getLastName();
        holder.friendDisplayName.setText(fullName);

        holder.my_friends_linear_layout.setOnClickListener(v -> {
            Intent intent = FriendsProfileActivity.getStartIntent(mContext, mFriends.get(position).getId());
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    class MyFriendsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_image)
        ImageView friendImage;
        @BindView(R.id.friend_display_name)
        TextView friendDisplayName;
        @BindView(R.id.my_friends_linear_layout)
        LinearLayout my_friends_linear_layout;

        MyFriendsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
