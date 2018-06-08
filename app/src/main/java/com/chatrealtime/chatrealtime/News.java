package com.chatrealtime.chatrealtime;

/**
 * Created by Phi Tup on 6/3/2018.
 */

public class News {

    public long timestamp;

    public News() {
    }

    public News(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
