package adnu.capstone.capstoneproject.PetOwner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import adnu.capstone.capstoneproject.Models.Schedule;
import adnu.capstone.capstoneproject.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class petSchedules extends Fragment{
    ViewPet viewPetActivity;


    RecyclerView my_pet_schedule_recyclerView;
    FirebaseRecyclerAdapter<Schedule,MyPetScheduleViewHolder> adapter;
    DatabaseReference scheduleRef;
    FirebaseUser auth;

    String [] scheduleSelection = new String[]{"Check-up", "Dentalprophylaxis",
            "Deworming", "Grooming", "Treatment", "Vaccination"};
    int photoID;
    String scheduleTitle;

    Schedule newSchedule;
    String saveDate, saveTime;
    int sDay,sMonth,sYear;
    int sHour,sMinute;
    static final int DATE_DIALOG_ID = 0,TIME_DIALOG_ID = 2;

    private int [] scheduleIcons = new int[]{
            R.drawable.checkuplogo, R.drawable.dentalprophylaxislogo, R.drawable.deworminglogo,
            R.drawable.groominglogo, R.drawable.treatmentlogo, R.drawable.vaccinationlogo, R.drawable.logo
    };

    String [] editItemSelection = new String[]{"Title", "Date/Time"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_pet_schedules, container, false);

        //Initialization for RecyclerView
        my_pet_schedule_recyclerView = (RecyclerView)rootView.findViewById(R.id.my_pet_schedule_recyclerView);
        my_pet_schedule_recyclerView.setHasFixedSize(true);
        my_pet_schedule_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Get the Current User
        auth = FirebaseAuth.getInstance().getCurrentUser();

        //Get Reference to Scheule Node
        viewPetActivity = (ViewPet)getActivity();
        String petKey = viewPetActivity.getKey();

        //Get Reference to the Pet node
        scheduleRef = FirebaseDatabase.getInstance().getReference().child("Schedule").child(auth.getUid()).child(petKey);
        Log.d("petSchedules", "petKey: " + petKey);

        final Calendar calendar = Calendar.getInstance();
        sYear = calendar.get(Calendar.YEAR);
        sMonth = calendar.get(Calendar.MONTH);
        sDay = calendar.get(Calendar.DAY_OF_MONTH);

        //Start Querying date, RecyclerView
        Query petSchedule = scheduleRef;
        adapter = new FirebaseRecyclerAdapter <Schedule, MyPetScheduleViewHolder>
                (Schedule.class, R.layout.pet_schedule_design, MyPetScheduleViewHolder.class,petSchedule){
            @Override
            protected void populateViewHolder(final MyPetScheduleViewHolder viewHolder, final Schedule model, final int position) {
                int pId = model.getPhotoId();

                Glide.with(getContext()).load(scheduleIcons[pId]).fitCenter().centerCrop()
                        .into(new GlideDrawableImageViewTarget(viewHolder.icon));

                viewHolder.scheduleTitle.setText(model.getTitle());
                viewHolder.scheduleTime.setText(model.getTime());
                viewHolder.scheduleDate.setText(model.getDate());

                viewHolder.overflow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(getContext(), viewHolder.overflow);
                        MenuInflater inflate = popup.getMenuInflater();
                        inflate.inflate(R.menu.schedule_menu_items, popup.getMenu());

                        photoID = model.getPhotoId();
                        scheduleTitle = model.getTitle();
                        String toBeEdit_Date = model.getDate();
                        sDay = Integer.parseInt(toBeEdit_Date.substring(0,2));
                        sMonth = Integer.parseInt(toBeEdit_Date.substring(3,5));
                        sYear = Integer.parseInt(toBeEdit_Date.substring(6,toBeEdit_Date.length()));

                        Toast.makeText(getContext(), sDay + "/" + sMonth + "/" + sYear,Toast.LENGTH_LONG).show();


                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.edit:{
                                        showEditSelection();
                                        break;
                                    }
                                    case R.id.delete:{
                                        if(adapter.getItemCount() == 1){
                                            adapter.getRef(0).removeValue(); // delete entry
                                        }else{
                                            adapter.getRef(position).removeValue();
                                        }
                                        break;
                                    }
                                    default:
                                        return false;
                                }
                                return false;
                            }

                            private void showEditSelection() {
                                final AlertDialog editSelectionDialog;
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select your choice");
                                builder.setSingleChoiceItems(editItemSelection, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which){
                                            case 0:
                                                editTitle();
                                                break;
                                            case 1:
                                                createDialog(DATE_DIALOG_ID).show();
                                                break;
                                        }
                                        //dialog.dismiss();
                                    }
                                });
                                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveSchedule();
                                        newSchedule = new Schedule(saveDate, saveTime, scheduleTitle, photoID);
                                        adapter.getRef(position).setValue(newSchedule);
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                editSelectionDialog = builder.create();
                                editSelectionDialog.show();
                            }

                            private void editTitle() {
                                final AlertDialog scheduleSelectionDialog;
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Schedule");

                                builder.setSingleChoiceItems(scheduleSelection, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        photoID = which;
                                        scheduleTitle = scheduleSelection[which];
                                        Toast.makeText(getContext(), scheduleTitle + " was selected", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                                builder.setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final AlertDialog.Builder addItemDialog = new AlertDialog.Builder(getContext());
                                        final EditText newScheduleTitle = new EditText(getContext());
                                        addItemDialog.setTitle("Add Schedule");
                                        newScheduleTitle.setSingleLine();
                                        FrameLayout container = new FrameLayout(getContext());
                                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(35,0,35,0);

                                        container.addView(newScheduleTitle,params);
                                        addItemDialog.setView(container);

                                        addItemDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(newScheduleTitle.getText().toString().length() != 0){
                                                    Toast.makeText(getContext(), newScheduleTitle.getText().toString(), Toast.LENGTH_LONG).show();
                                                    scheduleTitle = newScheduleTitle.getText().toString();
                                                    photoID = 6;
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
                        popup.show();
                    }
                });

            }
        };
        my_pet_schedule_recyclerView.setAdapter(adapter);

        return rootView;
    }


    public static class MyPetScheduleViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        CircleImageView icon;
        TextView scheduleTitle, scheduleDate, scheduleTime;
        ImageView overflow;

        public MyPetScheduleViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            icon = (CircleImageView) itemView.findViewById(R.id.scheduleIcon);
            scheduleTitle = (TextView)itemView.findViewById(R.id.scheduleTitle);
            scheduleDate = (TextView)itemView.findViewById(R.id.scheduleDate);
            scheduleTime = (TextView)itemView.findViewById(R.id.scheduleTime);
            overflow = (ImageView)itemView.findViewById(R.id.overflowMenu);
        }
    }
    public Dialog createDialog(int id) {
        if(id == DATE_DIALOG_ID)
            return new DatePickerDialog(getContext(), datePickerListener, sYear, sMonth-1, sDay);
        if(id == TIME_DIALOG_ID)
            return new TimePickerDialog(getContext(), timePickerListener, sHour,sMinute, false);
        return null;
    }

    public TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    sHour= hourOfDay;
                    sMinute = minute;
                }
            };
    public  DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            sYear = year;
            sMonth = monthOfYear + 1;
            sDay = dayOfMonth;
            createDialog(TIME_DIALOG_ID).show();
        }
    };

    public void saveSchedule(){
        String d = String.valueOf(sDay);
        String m = String.valueOf(sMonth);
        String y = String.valueOf(sYear);
        String h = String.valueOf(sHour);
        String min = String.valueOf(sMinute);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String dateInString = d + "-" + m + "-" + y + " " + h + ":" + min + " AM";
        Date date = null;
        String processDate = "";
        try {
            date = formatter.parse(dateInString);
            processDate = formatter.format(date);
            Log.d("ViewPet", "Date " + saveDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        saveDate = processDate.substring(0,10);
        saveTime = processDate.substring(11, processDate.length());

        Log.d("petSchedules", "TestingDate " + saveDate);
    }
}
