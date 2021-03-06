package com.chatrealtime.chatrealtime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mUserRef;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AnhXa();

        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("App Chat");

        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

        //Tabs

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            SendtoStart();
        }else{
            mUserRef.child("online").setValue("true");
        }




    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void SendtoStart() {
        Intent StartIntent = new Intent(MainActivity.this , StartActivity.class);
        startActivity(StartIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu , menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn){

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            SendtoStart();
        }

        if(item.getItemId() == R.id.main_settings_btn){

            Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsIntent);

        }

        if(item.getItemId() == R.id.main_all_btn){

            Intent settingsIntent = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(settingsIntent);

        }

        if(item.getItemId() == R.id.main_language_btn){

            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_language , null);

            mView.findViewById(R.id.VietNam).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ChangeLanguage("vi");

                }
            });

            mView.findViewById(R.id.USA).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ChangeLanguage("en");


                }
            });

            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();

        }

        return true;
    }

    public void ChangeLanguage(String Language){

        Locale locale = new Locale(Language);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(
                config,
                getBaseContext().getResources().getDisplayMetrics()
        );

        Intent inten = new Intent(MainActivity.this , MainActivity.class);
        startActivity(inten);

    }

    private void AnhXa(){
        mToolbar = findViewById(R.id.main_page_toolbar);
        mViewPager = findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter  = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager = findViewById(R.id.main_tabPager);
        mTabLayout = findViewById(R.id.main_tabs);

    }

}
