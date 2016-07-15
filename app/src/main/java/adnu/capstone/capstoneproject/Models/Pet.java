package adnu.capstone.capstoneproject.Models;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Martinez Edwin on 6/24/2016.
 */
public class Pet {
    String name, birthday, sex, specie, breed, path, petId;

    public Pet(String name, String birthday, String sex, String specie, String breed, String path,String petId) {
        this.name = name;
        this.birthday = birthday;
        this.sex = sex;
        this.breed = breed;
        this.specie = specie;
        this.path = path;
        this.petId = petId;
    }
    public Pet(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
