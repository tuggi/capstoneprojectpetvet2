package adnu.capstone.capstoneproject.PetOwner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import adnu.capstone.capstoneproject.Models.Pet;
import adnu.capstone.capstoneproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class petBasicProfile extends Fragment {
    ViewPet viewPetActivity;
    String petName, birthday, Sex, specie, breed;
    int age;

    //Layout
    TextView pPetName, pBirthday, pSex, pSpecie, pBreed, pAge;

    //Firebase
    FirebaseUser auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_pet_basic_profile, container, false);

        pPetName    =   (TextView)rootview.findViewById(R.id.petProfileName);
        pBirthday   =   (TextView)rootview.findViewById(R.id.petProfileBirthday);
        pSex        =   (TextView)rootview.findViewById(R.id.petProfileSex);
        pSpecie     =   (TextView)rootview.findViewById(R.id.petProfileSpecie);
        pBreed      =   (TextView)rootview.findViewById(R.id.petProfileBreed);
        pAge        =   (TextView)rootview.findViewById(R.id.petProfileAge);




        auth = FirebaseAuth.getInstance().getCurrentUser();
        viewPetActivity = (ViewPet)getActivity();
        String petKey = viewPetActivity.getKey();

        FirebaseDatabase.getInstance().getReference().child("Pet").child(auth.getUid()).child(petKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("petBasicProfile", "petProfile" + dataSnapshot);
                Pet myPet = dataSnapshot.getValue(Pet.class);
                pPetName.setText(myPet.getName());
                pBirthday.setText(myPet.getBirthday());
                pSex.setText(myPet.getSex());
                pSpecie.setText(myPet.getSpecie());
                pBreed.setText(myPet.getBreed());
                pAge.setText("Unknown for now");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return rootview;
    }




}
