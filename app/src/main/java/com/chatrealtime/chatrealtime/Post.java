package com.chatrealtime.chatrealtime;

/**
 * Created by Phi Tup on 6/7/2018.
 */

public class Post {

    public long seen;
    public long comment;
    public long like;
    public String date;

    public Post() {
    }

    public Post(long seen, long comment, long like, String date) {
        this.seen = seen;
        this.comment = comment;
        this.like = like;
        this.date = date;
    }

    public long getSeen() {
        return seen;
    }

    public void setSeen(long seen) {
        this.seen = seen;
    }

    public long getComment() {
        return comment;
    }

    public void setComment(long comment) {
        this.comment = comment;
    }

    public long getLike() {
        return like;
    }

    public void setLike(long like) {
        this.like = like;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
