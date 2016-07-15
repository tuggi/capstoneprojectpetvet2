package adnu.capstone.capstoneproject.PetOwner;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import adnu.capstone.capstoneproject.BaseActivity;
import adnu.capstone.capstoneproject.R;
import adnu.capstone.capstoneproject.loginPage;

public class MainActivity extends BaseActivity implements AHBottomNavigation.OnTabSelectedListener {
    private static final String TAG = "EmailPassword";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mRootRef; // node

    //Views
    FloatingActionButton floatingActionButton;
    Toolbar toolbar;
    AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        //Using Toolbar as Actionbar
        toolbar = (Toolbar)findViewById(R.id.mainforpetownerToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("   Home");
        getSupportActionBar().setIcon(R.drawable.toolbarlogoviolet);

        petOwnerFragmentMain fragment = new petOwnerFragmentMain();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        //Floating action button setup
        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddPet.class));
            }
        });

        //Setup AHBottomNavigation
        bottomNavigation = (AHBottomNavigation)findViewById(R.id.myBottomNavigation_ID);
        createNavItems();
        bottomNavigation.setOnTabSelectedListener(this);


        mRootRef = FirebaseDatabase.getInstance().getReference();
        

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    // startActivity(new Intent(loginPage.this, MainActivity.class));
                }else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    signOut();
                }
            }
        };

    }

    private void createNavItems() {
        //Create Items
        AHBottomNavigationItem myPetItem = new AHBottomNavigationItem("Home", R.drawable.ic_mypet);
        AHBottomNavigationItem clinics = new AHBottomNavigationItem("Clinics", R.drawable.ic_clinics);
        AHBottomNavigationItem myAppointments = new AHBottomNavigationItem("Appointments", R.drawable.ic_appointments);
        AHBottomNavigationItem myProfile = new AHBottomNavigationItem("Profile", R.drawable.ic_myprofile);

        //Add to bottomNavigation
        bottomNavigation.addItem(myPetItem);
        bottomNavigation.addItem(clinics);
        bottomNavigation.addItem(myAppointments);
        bottomNavigation.addItem(myProfile);

        //Set backgroundColor
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        //Set the first layout to see
        bottomNavigation.setCurrentItem(0);

        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#FFA726"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));


    }
    @Override
    public void onTabSelected(int position, boolean wasSelected) {
        if(position == 0){
            petOwnerFragmentMain fragment = new petOwnerFragmentMain();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            floatingActionButton.show();

        }else if(position == 1){
            petOwnerFragmentClinic fragment = new petOwnerFragmentClinic();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            floatingActionButton.hide();

        }else if(position == 2){
            petOwnerFragmentAppointments fragment = new petOwnerFragmentAppointments();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            floatingActionButton.hide();

        }if(position == 3){
            floatingActionButton.hide();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void signOut(){
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this,loginPage.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_adminpanel,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.Logout:{
                signOut();
                finish();
                break;
            }
            case R.id.settings:{
                Toast.makeText(MainActivity.this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm Exit...");
        alertDialog.setMessage("Are you sure you want to close this application?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

}
