package adnu.capstone.capstoneproject.Admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import adnu.capstone.capstoneproject.BaseActivity;
import adnu.capstone.capstoneproject.Models.Pet;
import adnu.capstone.capstoneproject.Models.VetClinic;
import adnu.capstone.capstoneproject.PetOwner.MainActivity;
import adnu.capstone.capstoneproject.PetOwner.ViewPet;
import adnu.capstone.capstoneproject.R;
import adnu.capstone.capstoneproject.loginPage;

public class adminPanel extends BaseActivity implements View.OnClickListener {
    Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference vetClinicRef;

    RecyclerView vetclinic_list_recycleview;
    FirebaseRecyclerAdapter<VetClinic, ClinicListViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Panel");
        getSupportActionBar().setIcon(R.drawable.toolbarlogoviolet);


        //Initialization for RecyclerView
        vetclinic_list_recycleview = (RecyclerView)findViewById(R.id.vet_list_recyclerView);
        vetclinic_list_recycleview.setHasFixedSize(true);
        vetclinic_list_recycleview.setLayoutManager(new LinearLayoutManager(this));



        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                }else {
                    signOut();
                }
            }
        };


        vetClinicRef = FirebaseDatabase.getInstance().getReference().child("VetClinic");
        Query vetClinicRefQuery = vetClinicRef;
        adapter = new FirebaseRecyclerAdapter<VetClinic, ClinicListViewHolder>
                (VetClinic.class, R.layout.viewclinic_layout_design, ClinicListViewHolder.class, vetClinicRefQuery) {
            @Override
            protected void populateViewHolder(ClinicListViewHolder viewHolder, VetClinic model, int position) {
                final DatabaseReference vetRefs = getRef(position);
                viewHolder.Name.setText(model.getName());
                viewHolder.Address.setText(model.getAddress());
                String downloadURL = model.getPhotoURL();
                Glide.with(adminPanel.this).load(downloadURL).fitCenter().centerCrop()
                        .into(new GlideDrawableImageViewTarget(viewHolder.imageProfile));

            }
        };
       vetclinic_list_recycleview.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);

    }
    public static class ClinicListViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        public ImageView imageProfile;
        public TextView Name, Address;
        public ClinicListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageProfile = (ImageView)itemView.findViewById(R.id.clinicProfilePhoto);
            Name = (TextView) itemView.findViewById(R.id.CLinicName);
            Address = (TextView) itemView.findViewById(R.id.ClinicAddress);


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
                break;
            }
            case R.id.settings:{
                Toast.makeText(adminPanel.this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(adminPanel.this,loginPage.class));
        finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:{
                startActivity(new Intent(this, AddVetClinic.class));
                break;
            }
        }

    }
}
