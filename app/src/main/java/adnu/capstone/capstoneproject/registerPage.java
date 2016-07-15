package adnu.capstone.capstoneproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import adnu.capstone.capstoneproject.Models.User;
import adnu.capstone.capstoneproject.Models.petOwner;
import adnu.capstone.capstoneproject.PetOwner.MainActivity;

public class registerPage extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "registerPage";


    private EditText email, password, firstName, LastName, phoneNumber,address;
    private TextView _loginLink;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

//        toolbar = (Toolbar) findViewById(R.id.registerPageToolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Register");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });



        //Fields
        email =         (EditText) findViewById(R.id.emailTField);
        password =      (EditText) findViewById(R.id.PasswordTField);
        firstName =     (EditText) findViewById(R.id.pet_form_name);
        LastName =      (EditText) findViewById(R.id.lastNameTField);
        phoneNumber =   (EditText) findViewById(R.id.phoneNumberField);
        address =       (EditText) findViewById(R.id.addressTField);

        //TextView

        _loginLink =     (TextView) findViewById(R.id.link_login);

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });


        //Button
//        FloatingActionButton signUpButton = (FloatingActionButton)findViewById(R.id.fab);
//        signUpButton.setOnClickListener(this);

        Button signUpButton = (Button) findViewById(R.id.btn_signup);
        signUpButton.setOnClickListener(this);

        // Firebase Auth Instance
        mAuth = FirebaseAuth.getInstance();

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mRootRef = FirebaseDatabase.getInstance().getReference();


        //Button onClickListeners
//        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup: {
                setUpInformation();
                //createAccount(email.getText().toString(),password.getText().toString());
                break;
            }
        }
    }

    private void setUpInformation() { // petOwner Setup
        String nEmail = email.getText().toString();
        String nPassword = password.getText().toString();
        String nFirstName = firstName.getText().toString();
        String nLastName = LastName.getText().toString();
        String nContactNumber = phoneNumber.getText().toString();
        String nAddress = address.getText().toString();
        //User tempUser = new User(nEmail,nPassword,nLastName,nFirstName,nAddress,nContactNumber);
        // String email, password, firstName,lastName,photoURL,address,contactNumber;
        petOwner temp = new petOwner(nEmail,nPassword,nFirstName,nLastName,"none",nAddress,nContactNumber);
        createAccount(temp);

    }

    private void createAccount(final petOwner newPetOwner) {
        Log.d(TAG, "createAccount:" + newPetOwner.getEmail());
        mAuth.createUserWithEmailAndPassword(newPetOwner.getEmail(),newPetOwner.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(registerPage.this, "Sign Up failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            //saveNewUser(task.getResult().getUser(),email,password);
                            // final User nNewUser = newUser;

                            User newUser = new User(newPetOwner.getEmail(),newPetOwner.getPassword(),"PetOwner");
                            //Add to users node
                            mRootRef.child("users").child(task.getResult().getUser().getUid()).setValue(newUser);
                            //Add to PetOwners node
                            mRootRef.child("PetOwners").child(task.getResult().getUser().getUid()).setValue(newPetOwner);

                            startActivity(new Intent(registerPage.this, MainActivity.class));
                            finish();


                           /* Map<String,Object> owner = new HashMap<String, Object>();
                            owner.put("Type", "petOwner");
                            mRootRef.child("users").child(task.getResult().getUser().getUid()).updateChildren(owner);*/

                            /*Map<String,Object> petOwnerData = new HashMap<String, Object>();
                            petOwnerData.put("Email",nNewUser.getEmail());
                            petOwnerData.put("Password",newUser.getPassword());
                            mRootRef.child("PetOwners").child(task.getResult().getUser().getUid()).setValue(petOwnerData);*/
                        }
                    }
                });

    } // [END create_user_with_email]


}
