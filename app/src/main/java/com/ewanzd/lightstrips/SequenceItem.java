package com.ewanzd.lightstrips;

public class SequenceItem {

    private long id;
    private int color;
    private int time;

    public SequenceItem() {

    }

    public SequenceItem(int color, int time) {
        this.color = color;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
