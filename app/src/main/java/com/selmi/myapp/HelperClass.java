package com.selmi.myapp;

public class HelperClass {
    String username, mail, password ,idUser ;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public HelperClass() {
    }

    public HelperClass(String idUser, String username, String mail, String password) {
        this.idUser=idUser;
        this.username = username;
        this.mail = mail;
        this.password = password;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
