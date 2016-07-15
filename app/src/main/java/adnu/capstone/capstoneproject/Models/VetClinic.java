package adnu.capstone.capstoneproject.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Martinez Edwin on 7/14/2016.
 */
public class VetClinic {
    String name, contactNumber, address, photoURL, email, password;

    public VetClinic(String name, String contactNumber, String address, String email, String photoURL, String password) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
        this.email = email;
        this.photoURL = photoURL;
        this.password = password;
    }

    public VetClinic() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void saveVetClinic(VetClinic newVetClinic, String key){
        //Save as NewUser
        User newUser = new User(newVetClinic.getEmail(),newVetClinic.getPassword(),"VetClinic");
        FirebaseDatabase.getInstance().getReference().child("users").child(key).setValue(newUser);

        //Save as NewClinic
        FirebaseDatabase.getInstance().getReference().child("VetClinic").child(key).setValue(newVetClinic);

    }
}
