package adnu.capstone.capstoneproject.PetOwner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;

import adnu.capstone.capstoneproject.CalendarFunction;
import adnu.capstone.capstoneproject.Models.Pet;
import adnu.capstone.capstoneproject.R;

public class AddPet extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddPetActivity";
    Toolbar toolbar;
    ImageView petPhoto;

    EditText name, birthday, sex, specie, breed;

    //setup Birthday
    int bYear,bMonth,bDate;
    static final int DIALOG_ID = 0;

    StorageReference storageRef; // root
    FirebaseAuth auth;

    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int IMAGE_CAMERA_REQUEST = 21;
    Uri downloadURL;

    CalendarFunction newCaledar;

    private Uri mFileUri = null;
    String[] ImageSelectionOptions = new String[]{"   From Cam", "   From Gallery"};
    String [] SexSelectionOptions = new String[]{"Male", "Female"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
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
        name = (EditText)findViewById(R.id.pet_form_name);
        birthday = (EditText)findViewById(R.id.pet_form_bday);
        sex = (EditText)findViewById(R.id.pet_form_sex);
        specie = (EditText)findViewById(R.id.pet_form_specie);
        breed = (EditText) findViewById(R.id.pet_form_breed);

        final Calendar calendar = Calendar.getInstance();
        newCaledar = new CalendarFunction();
        newCaledar.setYear_x(calendar.get(Calendar.YEAR));
        newCaledar.setMonth_x(calendar.get(Calendar.MONTH));
        newCaledar.setDay_x(calendar.get(Calendar.DAY_OF_MONTH));

       // birthday.setText(bYear + "/" + bMonth + "/" + bDate);
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);

                bYear = newCaledar.getYear_x();
                bMonth = newCaledar.getMonth_x();
                bDate = newCaledar.getDay_x();
                birthday.setText(bYear + "/" + bMonth + "/" + bDate);

            }
        });

        sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog genderDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPet.this);
                builder.setTitle("Select Gender");

                builder.setSingleChoiceItems(SexSelectionOptions, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            switch (which){
                                case 0:
                                    sex.setText("Male");
                                    break;
                                case 1:
                                    sex.setText("Female");
                                    break;

                            }
                        dialog.dismiss();
                    }
                });
                    genderDialog = builder.create();
                    genderDialog.show();

            }

        });

        auth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        petPhoto = (ImageView)findViewById(R.id.petPhotoBG);

        FloatingActionButton addPhoto = (FloatingActionButton)findViewById(R.id.fabCamera);
        addPhoto.setOnClickListener(this);

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


    //Birthday Dialog Picker
    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_ID)
            return new DatePickerDialog(this, newCaledar.datePickerListener, bYear,bMonth,bDate);
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_pet_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.save:{
                setupPetDetails();
                onBackPressed();
                break;
            }
            case R.id.cancel:{
                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupPetDetails(){
        String sName = name.getText().toString();
        String sBirthday = birthday.getText().toString();
        String sSex = sex.getText().toString();
        String sSpecie = specie.getText().toString();
        String sBreed = breed.getText().toString();

        savePetDetails(sName,sBirthday,sSex,sSpecie,sBreed);
    }

    private void savePetDetails(final String sName, final String sBirthday, final String sSex, final String sSpecie, final String sBreed) {
        final String key =  FirebaseDatabase.getInstance().getReference().child(auth.getCurrentUser().getUid()).push().getKey(); // Pet Key



        StorageReference photoRef = storageRef.child("PetOwner").child(auth.getCurrentUser().getUid()).child(key)
                .child(mFileUri.getLastPathSegment());
        UploadTask uploadTask = photoRef.putFile(mFileUri);

        Toast.makeText(AddPet.this, mFileUri.getLastPathSegment(), Toast.LENGTH_LONG).show();
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadURL =  taskSnapshot.getDownloadUrl();
                Pet newPet = new Pet(sName,sBirthday,sSex,sSpecie,sBreed,downloadURL.toString(),key);
                FirebaseDatabase.getInstance().getReference().child("Pet").child(auth.getCurrentUser().getUid())
                        .child(key).setValue(newPet);
            }
        });


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
                    petPhoto.setImageBitmap(image);
                    Toast.makeText(this, mFileUri.getLastPathSegment(), Toast.LENGTH_LONG).show();

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
                                petPhoto.setImageBitmap(image);
                                Toast.makeText(this, mFileUri.getLastPathSegment().toString(), Toast.LENGTH_LONG).show();

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
