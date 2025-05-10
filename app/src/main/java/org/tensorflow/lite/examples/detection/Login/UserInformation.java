package org.tensorflow.lite.examples.detection.Login;

public class UserInformation {

    public String Name, age, emergencyNum,location;

    public UserInformation() {

    }


    public UserInformation(String Name, String age, String emergencyNum,String location) {
        this.Name = Name;
        this.age = age;
        this.emergencyNum = emergencyNum;
        this.location = location;
     }
}