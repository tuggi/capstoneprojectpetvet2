package adnu.capstone.capstoneproject.Models;

/**
 * Created by Martinez Edwin on 6/21/2016.
 */
public class User {
    public String email,password,type;

    public User(String email, String password, String type) {
        this.email = email;
        this.password = password;
        this.type = type;
    }
    public  User(){

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
