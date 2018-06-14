package com.chatrealtime.chatrealtime;

/**
 * Created by Phi Tup on 6/3/2018.
 */

public class News {

    private String image;
    private String status;
    private long timestamp;

    public News() {
    }

    public News(String image, String status, long timestamp) {
        this.image = image;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
