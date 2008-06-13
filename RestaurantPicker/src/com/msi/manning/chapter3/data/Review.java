package com.msi.manning.chapter3.data;

import java.util.Date;

import com.sun.syndication.feed.impl.ToStringBean;

public class Review
{    
    private String name;
    private String title;
    private String author;
    private String link;
    private String location;
    private String phone;
    private String rating;    
    private Date date;
    private String content;
    private String cuisine;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    
    public String toString() {
        return (new ToStringBean(Review.class, this)).toString();
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getCuisine() {
        return cuisine;
    }
    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }
    
    /* StackOverflowError s
    public boolean equals(Object obj) {
        EqualsBean eBean = new EqualsBean(Review.class,this);
        return eBean.beanEquals(obj);
    }

    public int hashCode() {
        EqualsBean equals = new EqualsBean(Review.class,this);
        return equals.beanHashCode();
    }
    */

    
}