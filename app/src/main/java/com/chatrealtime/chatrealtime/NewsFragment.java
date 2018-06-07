package com.chatrealtime.chatrealtime;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class NewsFragment extends Fragment {

    private FloatingActionButton addPostBtn;
    private RecyclerView mNewsList;

    private View mMainView;

    private DatabaseReference mUserDatabase , mPostDatabase , mNewsDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_User_id;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.add_post_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent NewsIntent = new Intent(getContext(),NewsActivity.class);
                startActivity(NewsIntent);


            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<News , NewsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<News, NewsViewHolder>(

                News.class,
                R.layout.news_posts_layout,
                NewsViewHolder.class,
                mPostDatabase

        ) {
            @Override
            protected void populateViewHolder(final NewsViewHolder newsViewHolder, News news, int i) {

                newsViewHolder.setDate(news.getDate());

                final String list_users_id = getRef(i).getKey();

                mUserDatabase.child(list_users_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("image").getValue().toString();

                        newsViewHolder.setName(userName);
                        newsViewHolder.setUserImage(userThumb, getContext());


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mNewsDatabase.child(list_users_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String status = dataSnapshot.child("status").getValue().toString();
                        String newsThumb = dataSnapshot.child("image").getValue().toString();

                        newsViewHolder.setStatus(status);
                        newsViewHolder.setNewsImage(newsThumb, getContext());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mNewsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public NewsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date){

            TextView userStatusView =  mView.findViewById(R.id.post_news_time);
            userStatusView.setText(date);

        }

        public void setName(String name){

            TextView userNameView =  mView.findViewById(R.id.user_news_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView =  mView.findViewById(R.id.user_news_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_user).into(userImageView);

        }

        public void setNewsImage(String thumb_image, Context ctx){

            ImageView userNewsView =  mView.findViewById(R.id.new_post_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_user).into(userNewsView);

        }

        public void setStatus(String status){

            TextView txtStatus = mView.findViewById(R.id.user_news_status);
            txtStatus.setText(status);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_news, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_User_id = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("post").child(mCurrent_User_id);
        mPostDatabase.keepSynced(true);

        mNewsDatabase = FirebaseDatabase.getInstance().getReference().child("post");
        mNewsDatabase.keepSynced(true);

//        mNewsList = mMainView.findViewById()
        mNewsList.setHasFixedSize(true);
        mNewsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }


}
