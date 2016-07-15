package adnu.capstone.capstoneproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import adnu.capstone.capstoneproject.Admin.adminPanel;
import adnu.capstone.capstoneproject.PetOwner.MainActivity;

public class loginPage extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;


    Button login;

    TextView reg1, reg2;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String whatType = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //Initialization
        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        login = (Button) findViewById(R.id.loginButton);
        reg1 = (TextView) findViewById(R.id.textDontHaveAccount);
        reg2 = (TextView) findViewById(R.id.textDontHaveAccountRegister);

        enablePersistence();

        // check if authenticated or not
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).
                            child("type").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("loginPage", "TYPE " + dataSnapshot.getValue(String.class));
                            whatType = dataSnapshot.getValue(String.class);
                            if (whatType.contentEquals("PetOwner")) {
                                startActivity(new Intent(loginPage.this, MainActivity.class));
                            } else if (whatType.contentEquals("ADMIN")) {
                                startActivity(new Intent(loginPage.this, adminPanel.class));
                            } else if (whatType.contentEquals("VetClinic")) {
                                mAuth.signOut();
                            } else {
                                mAuth.signOut();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

            //onClickListeners
            reg1.setOnClickListener(this);
            reg2.setOnClickListener(this);
            login.setOnClickListener(this);
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

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(loginPage.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginButton:{
                signIn(mEmailField.getText().toString(),mPasswordField.getText().toString());
                break;
            }
            case R.id.textDontHaveAccount:{
                startActivity(new Intent(loginPage.this, registerPage.class));
                break;
            }
            case R.id.textDontHaveAccountRegister:{
                startActivity(new Intent(loginPage.this, registerPage.class));
                break;
            }

        }
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