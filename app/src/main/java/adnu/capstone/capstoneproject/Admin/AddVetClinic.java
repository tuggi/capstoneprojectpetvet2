package adnu.capstone.capstoneproject.Admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import adnu.capstone.capstoneproject.BaseActivity;
import adnu.capstone.capstoneproject.Models.Pet;
import adnu.capstone.capstoneproject.Models.User;
import adnu.capstone.capstoneproject.Models.VetClinic;
import adnu.capstone.capstoneproject.PetOwner.MainActivity;
import adnu.capstone.capstoneproject.R;
import adnu.capstone.capstoneproject.loginPage;

public class AddVetClinic extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AddVetClinic";


    //Layout
    EditText email,password, name, address, contactNumber;
    ImageView vetclinicPhotoBg;
    Toolbar toolbar;

    //Firebase
    StorageReference storageRef; // root
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    DatabaseReference rootRef;

    //Photo
    private Uri mFileUri = null;
    String[] ImageSelectionOptions = new String[]{"   From Cam", "   From Gallery"};
    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int IMAGE_CAMERA_REQUEST = 21;
    Uri downloadURL;

    //Setup
    String vName, vEmail ="", vPassword="", vAddress, vContactNumber;
    VetClinic newVetClinic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vet_clinic);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        //Form
        email = (EditText)findViewById(R.id.clinic_form_email);
        password = (EditText)findViewById(R.id.clinic_form_password);
        name = (EditText)findViewById(R.id.clinic_form_name);
        address = (EditText)findViewById(R.id.clinic_form_address);
        contactNumber = (EditText)findViewById(R.id.clinic_form_contactNumber);
        vetclinicPhotoBg = (ImageView)findViewById(R.id.vetclinicPhotoBG);

        //Firebase
        auth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).
                            child("type").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("loginPage", "TYPE " + dataSnapshot.getValue(String.class));
                            String whatType = dataSnapshot.getValue(String.class);
                            if(!whatType.contentEquals("admin")){
                                startActivity(new Intent(AddVetClinic.this, loginPage.class));
                                SigninAdmin("admin@admin.com", "admin123");
                           }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        };

        FloatingActionButton addPhoto = (FloatingActionButton)findViewById(R.id.fabCamera);
        addPhoto.setOnClickListener(this);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_vetclinic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.save:{
                setupVetClinicDetails();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupVetClinicDetails() {
        vName = name.getText().toString();
        vEmail = email.getText().toString();
        vPassword = password.getText().toString();
        vAddress = address.getText().toString();
        vContactNumber = contactNumber.getText().toString();

        saveVetClinic();

    }
    private void saveVetClinic(){
            auth.createUserWithEmailAndPassword(vEmail,vPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Toast.makeText(AddVetClinic.this, task.getResult().toString(), Toast.LENGTH_LONG).show();
                            }else {
                                    Toast.makeText(AddVetClinic.this, "Account successfully Created", Toast.LENGTH_LONG).show();
                                    StorageReference photoRef = storageRef.child("VetClinic").child(auth.getCurrentUser().getUid())
                                        .child(mFileUri.getLastPathSegment());

                                    UploadTask uploadTask = photoRef.putFile(mFileUri);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            downloadURL = taskSnapshot.getDownloadUrl();
                                            newVetClinic = new VetClinic(vName, vContactNumber, vAddress, vEmail, downloadURL.toString(), vPassword);
                                            newVetClinic.saveVetClinic(newVetClinic, auth.getCurrentUser().getUid());
                                            SigninAdmin("admin@admin.com", "admin123");
                                            startActivity(new Intent(AddVetClinic.this, adminPanel.class));

                                        }
                                    });


                            }
                        }

                    });

    }
    private void SigninAdmin(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("AddVetClinic","SigninAdmin" +  task.getResult());
                        }else{
                        }
                    }
                });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabCamera:{
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, ImageSelectionOptions);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select Image");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            launchImageCameraIntent();
                        }else{
                            launchImageGalleryIntent();
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
        }
    }

    private void launchImageCameraIntent() {
        // Create intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Choose file storage location
        File file = new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString() + ".jpeg");
        mFileUri = Uri.fromFile(file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        // Launch intent
        startActivityForResult(takePictureIntent, IMAGE_CAMERA_REQUEST);
    }

    private void launchImageGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                mFileUri = data.getData();
                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(mFileUri);
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    vetclinicPhotoBg.setImageBitmap(image);
                    //Toast.makeText(this, mFileUri.getLastPathSegment(), Toast.LENGTH_LONG).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open the image", Toast.LENGTH_LONG).show();
                }
            }else{
                if (requestCode == IMAGE_CAMERA_REQUEST) {
                    if (resultCode == RESULT_OK) {
                        if (mFileUri != null) {
                            InputStream inputStream;
                            try {
                                inputStream = getContentResolver().openInputStream(mFileUri);
                                Bitmap image = BitmapFactory.decodeStream(inputStream);
                                vetclinicPhotoBg.setImageBitmap(image);
                               // Toast.makeText(this, mFileUri.getLastPathSegment().toString(), Toast.LENGTH_LONG).show();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();

                            }
                        } else {
                            Log.w(TAG, "File URI is null");
                        }
                    } else {
                        Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }



}
