package com.example.inclass12;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class Contact {
    String name;
    String email;
    String phone;
    String id;
    String img_url;

    public Contact(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.img_url = null;
    }

    public Contact() {

    }


    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> hmap = new HashMap<>();
        hmap.put("name", this.name);
        hmap.put("email", this.email);
        hmap.put("phone", this.phone);
        hmap.put("id", this.id);
        hmap.put("img_url", this.img_url);

        return hmap;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", id='" + id + '\'' +
                ", img_url='" + img_url + '\'' +
                '}';
    }
}
