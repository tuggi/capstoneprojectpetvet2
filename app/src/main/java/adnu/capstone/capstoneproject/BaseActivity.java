package adnu.capstone.capstoneproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Martinez Edwin on 7/12/2016.
 */
public class BaseActivity extends AppCompatActivity{

    private ProgressDialog progressDialog;
    static boolean persistence = false;

    public void enablePersistence(){
        if(!persistence){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            persistence = true;
        }
    }

    public void showNotification(String contentTitle, String contentText){
        Intent intent = new Intent();
        PendingIntent pIntent = PendingIntent.getActivity(this,0,intent,0);
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("TickerTitle")
                .setContentTitle("")
                .setContentText("")
                .setSmallIcon(R.drawable.toolbarlogo)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }
    public void hideProgressDialog(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

}