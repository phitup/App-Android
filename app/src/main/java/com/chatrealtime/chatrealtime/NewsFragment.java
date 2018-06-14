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
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class NewsFragment extends Fragment {

    private FloatingActionButton addPostBtn;
    private RecyclerView mNewsList;

    private View mMainView;

    private DatabaseReference mUserDatabase , mPostDatabase , mNewsDatabase , mFriendsDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_User_id;

    private Map postMap;


    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



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


        Query conversationQuery = mPostDatabase.orderByChild("status");
        mNewsDatabase = FirebaseDatabase.getInstance().getReference().child("post").child(mCurrent_User_id).child("status");
        final FirebaseRecyclerAdapter<News , NewsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<News, NewsViewHolder>(

                News.class,
                R.layout.news_posts_layout,
                NewsViewHolder.class,
                mNewsDatabase

        ) {
            @Override
            protected void populateViewHolder(final NewsViewHolder viewHolder, News model, int position) {




                mNewsDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        News news;

                        news = dataSnapshot.getValue(News.class);

                        viewHolder.setStatus(news.getStatus());


                        viewHolder.setUserImage(news.getImage(),getContext());

//                        Picasso.with(getContext()).load(news.getImage()).placeholder(R.drawable.add_100).into();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mNewsList.setAdapter(firebaseRecyclerAdapter);

        mNewsDatabase = FirebaseDatabase.getInstance().getReference().child("post").child(mCurrent_User_id).child("status");
       mNewsDatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               String status = dataSnapshot.getValue().toString();
//               Toast.makeText(getContext(), "Hello: "+status, Toast.LENGTH_SHORT).show();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });


    }


    public static class NewsViewHolder extends RecyclerView.ViewHolder {

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

        public void setStatus(String status){

            TextView userStatus = mView.findViewById(R.id.user_news_status);
            userStatus.setText(status);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView =  mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_user).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView =  mView.findViewById(R.id.user_single_online_ioon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_news, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_User_id = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrent_User_id);
        mUserDatabase.keepSynced(true);

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("post").child(mCurrent_User_id);
        mPostDatabase.keepSynced(true);

        mNewsDatabase = FirebaseDatabase.getInstance().getReference().child("post");
//        mNewsDatabase = FirebaseDatabase.getInstance().getReference().child("post").child(mCurrent_User_id).child("status");
        mNewsDatabase.keepSynced(true);

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_User_id);
        mFriendsDatabase.keepSynced(true);

        mNewsList = mMainView.findViewById(R.id.post_news_list);
        mNewsList.setHasFixedSize(true);
        mNewsList.setLayoutManager(new LinearLayoutManager(getContext()));

        postMap = new HashMap<>();
        postMap.put("status" , "status");
        postMap.put("image" , "image");
        postMap.put("timestamp" , ServerValue.TIMESTAMP);

        mPostDatabase.updateChildren(postMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError != null){

                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                }

            }
        });

        return mMainView;
    }


}
