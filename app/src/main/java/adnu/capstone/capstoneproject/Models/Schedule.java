package adnu.capstone.capstoneproject.Models;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Martinez Edwin on 7/6/2016.
 */
public class Schedule {
    String date; // July 06, 2016
    String time; // 11:00 PM
    String title;
    int photoId;

    public Schedule(String date, String time, String title, int photoId) {
        this.date = date;
        this.time = time;
        this.title = title;
        this.photoId = photoId;
    }

    public Schedule(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public void saveSchedule(Schedule schedule, String userKey, String petKey){
        FirebaseDatabase.getInstance().getReference().child("Schedule").child( userKey).child(petKey).push().setValue(schedule);
    }

}
