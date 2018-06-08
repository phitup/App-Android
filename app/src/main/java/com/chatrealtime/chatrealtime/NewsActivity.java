package com.chatrealtime.chatrealtime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class NewsActivity extends AppCompatActivity {

    private DatabaseReference mNewsDatabase , mPostDatabase;
    private FirebaseUser mCurrentUser;

    private Toolbar newPostToolbar;
    private EditText editText;
    private Button post_btn;
    private ImageButton image_post_btn;
    private ProgressDialog mProgressDialog;

    private StorageReference mImageStorage;

    private String mCurrent_User_id;
    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        AnhXa();

        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");

        mPostDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(NewsActivity.this).load(image).placeholder(R.drawable.add_100).into(image_post_btn);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = editText.getText().toString();



                Picasso.with(NewsActivity.this).load(R.drawable.add_100).into(image_post_btn);
                mPostDatabase.child("status").setValue(name);

            }
        });

        image_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });

        Map postMap = new HashMap<>();
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



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);

            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                mProgressDialog = new ProgressDialog(NewsActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                final String current_user_id = mCurrentUser.getUid();


                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                StorageReference filepath = mImageStorage.child("news_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("news_images").child("thumbs").child(current_user_id + ".jpg");



                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            mPostDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        mProgressDialog.dismiss();
                                        Toast.makeText(NewsActivity.this, "successful", Toast.LENGTH_SHORT).show();

                                    }else{

                                        mProgressDialog.dismiss();
                                        Toast.makeText(NewsActivity.this, "failed", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

//                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
//                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
//
//                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
//
//                                    if(thumb_task.isSuccessful()){
//
//                                        Map update_hashMap = new HashMap();
//                                        update_hashMap.put("image", download_url);
//                                        update_hashMap.put("thumb_image", thumb_downloadUrl);
//
//                                        mNewsDatabase.child("post").child(current_user_id).updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                if(task.isSuccessful()){
//
//                                                    mProgressDialog.dismiss();
//
//                                                }
//
//                                            }
//                                        });
//
//
//                                    } else {
//
//                                        Toast.makeText(NewsActivity.this, "Error in uploading thumbnail.", Toast.LENGTH_LONG).show();
//                                        mProgressDialog.dismiss();
//
//                                    }
//
//
//                                }
//                            });



                        } else {

                            Toast.makeText(NewsActivity.this, "Error in uploading.", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                        }

                    }
                });



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

    private void AnhXa(){

            editText = findViewById(R.id.editText);
            post_btn = findViewById(R.id.post_btn);
            image_post_btn = findViewById(R.id.new_post_image);

            newPostToolbar = findViewById(R.id.new_post_toolbar);
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            mCurrent_User_id = mCurrentUser.getUid();


            mNewsDatabase = FirebaseDatabase.getInstance().getReference();
            mNewsDatabase.keepSynced(true);

            mPostDatabase = FirebaseDatabase.getInstance().getReference().child("post").child(mCurrent_User_id);

            mImageStorage = FirebaseStorage.getInstance().getReference();




    }

}
