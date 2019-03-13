package de.proneucon.myfirebase;

import java.io.Serializable;

public class Message {

    /**
     * Wir versuchen ein Objekt in die Firebase DB einzufügen
     * ...dafür bereiten wir das anlegen hier vor
     */
    private String user;
    private String text;


    //CONSTRUCTOR
    public Message() {
    }

    public Message(String user, String text) {
        this.user = user;
        this.text = text;
    }

    //GETTER UND SETTER
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
