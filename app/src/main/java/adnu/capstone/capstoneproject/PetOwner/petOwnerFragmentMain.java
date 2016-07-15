package adnu.capstone.capstoneproject.PetOwner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import adnu.capstone.capstoneproject.Models.Pet;
import adnu.capstone.capstoneproject.R;

import com.firebase.ui.database.FirebaseRecyclerAdapter;


public class petOwnerFragmentMain extends Fragment {
    RecyclerView my_pet_list_recyclerView;
    DatabaseReference petsRef;
    FirebaseUser auth;
    FirebaseRecyclerAdapter<Pet, MyPetListViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View rootView =  inflater.inflate(R.layout.fragment_pet_owner_fragment_main, container, false);


        //Initialization for RecyclerView
        my_pet_list_recyclerView = (RecyclerView) rootView.findViewById(R.id.my_pet_list_recyclerView);
        my_pet_list_recyclerView.setHasFixedSize(true);
        my_pet_list_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Get the Current User
        auth = FirebaseAuth.getInstance().getCurrentUser();

        //Get Reference to Pet Node
        petsRef = FirebaseDatabase.getInstance().getReference().child("Pet").child(auth.getUid());

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query petQuery = petsRef;
        adapter = new FirebaseRecyclerAdapter<Pet, MyPetListViewHolder>
                (Pet.class, R.layout.viewpet_layout_design, MyPetListViewHolder.class, petQuery) {
            @Override
            protected void populateViewHolder(final MyPetListViewHolder myPetListViewHolder, Pet pet, final int i) {
                    myPetListViewHolder.petName.setText(pet.getName());
                    String downloadURL = pet.getPath();

                    Glide.with(getContext()).load(downloadURL).fitCenter().centerCrop()
                            .into(new GlideDrawableImageViewTarget(myPetListViewHolder.imageProfile));
                        myPetListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String petKey = adapter.getRef(i).getKey();
                                //Toast.makeText(getContext(),petKey,Toast.LENGTH_LONG).show();
                                Intent pass = new Intent(getContext().getApplicationContext(), ViewPet.class);
                                pass.putExtra("Reference", petKey);
                                startActivity(pass);
                            }

                        });
            }
        };
        my_pet_list_recyclerView.setAdapter(adapter);

    }

    public static class MyPetListViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        public ImageView imageProfile;
        public TextView petName,petSex;
        public MyPetListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            imageProfile = (ImageView)itemView.findViewById(R.id.petImageProfile);
            petName = (TextView)itemView.findViewById(R.id.petName);

        }


    }
}
