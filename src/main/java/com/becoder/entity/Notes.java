package com.becoder.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Notes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;

    private String category;

    private String description;

//    @Lob
//    private byte[] file;


//    public byte[] getFile() {
//        return file;
//    }
//
//    public void setFile(byte[] file) {
//        this.file = file;
//    }

    private String image;
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private LocalDate date;
    @ManyToOne
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    @Override
    public String toString() {
        return "Notes{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", date=" + date +
                ", user=" + user +
                '}';
    }
}
