package adnu.capstone.capstoneproject.PetOwner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import adnu.capstone.capstoneproject.Models.Pet;
import adnu.capstone.capstoneproject.Models.Schedule;
import adnu.capstone.capstoneproject.R;

public class ViewPet extends AppCompatActivity {

    //Activity Components
    private ViewPager viewPager;
    private TabLayout tabLayout;
    FloatingActionButton fab;
    ImageView petImageProfile;
    Toolbar toolbar;

    //Firebase related
    FirebaseUser auth;
    String key;

    /*  Schedule Setup  */
    String [] scheduleSelection = new String[]{"Check-up", "Dentalprophylaxis",
            "Deworming", "Grooming", "Treatment", "Vaccination"};

    /* Schedule Edit */
    static final int DATE_DIALOG_ID = 0,TIME_DIALOG_ID = 2;
    String scheduleTitle, currentTime;
    int photoID, sDay, sMonth, sYear, sHour, sMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pet);

        //Toolbar Setup
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                onBackPressed();
            }
        });

        petImageProfile = (ImageView)findViewById(R.id.petImageProfile);

        //Get the Current User
        auth = FirebaseAuth.getInstance().getCurrentUser();

        //ViewPager
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //Tab Icons
       /* tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);*/

        /*  Schedule Setup  */
        final Calendar calendar = Calendar.getInstance();
        sYear = calendar.get(Calendar.YEAR);
        sMonth = calendar.get(Calendar.MONTH);
        sDay = calendar.get(Calendar.DAY_OF_MONTH);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                final AlertDialog scheduleSelectionDialog;
                final AlertDialog.Builder builder = new AlertDialog.Builder(ViewPet.this);
                builder.setTitle("Select Schedule");

                builder.setSingleChoiceItems(scheduleSelection, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        photoID = which;
                        scheduleTitle = scheduleSelection[which];
                        showDialog(DATE_DIALOG_ID);
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final AlertDialog.Builder addItemDialog = new AlertDialog.Builder(ViewPet.this);
                        final EditText newScheduleTitle = new EditText(ViewPet.this);
                        addItemDialog.setTitle("Add Schedule");
                        newScheduleTitle.setSingleLine();
                        FrameLayout container = new FrameLayout(ViewPet.this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(35,0,35,0);

                        container.addView(newScheduleTitle,params);
                        addItemDialog.setView(container);

                        addItemDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(newScheduleTitle.getText().toString().length() != 0){
                                    Toast.makeText(ViewPet.this, newScheduleTitle.getText().toString(), Toast.LENGTH_LONG).show();
                                    scheduleTitle = newScheduleTitle.getText().toString();
                                    photoID = 6;
                                    showDialog(DATE_DIALOG_ID);
                                    dialog.dismiss();
                                }
                            }
                        });
                        addItemDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        addItemDialog.show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                scheduleSelectionDialog = builder.create();
                scheduleSelectionDialog.show();
            }
        });
        fab.hide();


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 2:
                        fab.show();
                        break;
                    default:
                        fab.hide();
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Intent rec = getIntent();
        key = rec.getExtras().getString("Reference");

        // Toast.makeText(getApplicationContext(),key,Toast.LENGTH_LONG).show();

        //Get Pet information
        FirebaseDatabase.getInstance().getReference().child("Pet").child(auth.getUid()).child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Pet myPet = dataSnapshot.getValue(Pet.class);
                        Glide.with(ViewPet.this).load(myPet.getPath()).fitCenter().centerCrop()
                                .into(new GlideDrawableImageViewTarget(petImageProfile));

                        Log.w("ViewPet", "dataSnapshot " + dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // For Notification

        SimpleDateFormat curTime = new SimpleDateFormat("dd-MM-yyyy h:mm a");
        String CurDate = curTime.format(Calendar.getInstance().getTime());
        currentTime = CurDate.substring(11, CurDate.length());

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new petBasicProfile(), "Profile");
        adapter.addFragment(new petRecords(), "Records");
        adapter.addFragment(new petSchedules(), "Schedules");
        viewPager.setAdapter(adapter);
    }

    //Open Dialog
    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DATE_DIALOG_ID)
            return new DatePickerDialog(this, datePickerListener, sYear, sMonth, sDay);
        if(id == TIME_DIALOG_ID)
            return new TimePickerDialog(this, timePickerListener, sHour,sMinute, false);
        return null;


    }
    //DatePicker
    private  DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            sYear = year;
            sMonth = monthOfYear + 1;
            sDay = dayOfMonth;
            showDialog(TIME_DIALOG_ID);;
        }
    };
    //TimePicker
    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    sHour = hourOfDay;
                    sMinute = minute;
                    saveSchedule();
                }
            };
    //SaveRecord
    public void saveSchedule(){
        String d = String.valueOf(sDay);
        String m = String.valueOf(sMonth);
        String y = String.valueOf(sYear);
        String h = String.valueOf(sHour);
        String min = String.valueOf(sMinute);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String dateInString = d + "-" + m + "-" + y + " " + h + ":" + min + " AM";
        Date date = null;
        String processDate = "", saveTime = "", saveDate ="";
        try {
            date = formatter.parse(dateInString);
            processDate = formatter.format(date);
            Log.d("ViewPet", "Date " + saveDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        saveDate = processDate.substring(0,10);
        saveTime = processDate.substring(11, processDate.length());
        Schedule newSchedule = new Schedule(saveDate, saveTime, scheduleTitle, photoID);
        newSchedule.saveSchedule(newSchedule,auth.getUid(),key);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public String getKey(){
        return key;
    }
}
