package com.ewanzd.lightstrips;

import java.util.List;

public class Sequence {

    private long _id;
    private String name;
    private List<SequenceItem> items;

    public Sequence() {
    }

    public Sequence(String name) {
        this.name = name;
    }

    public long get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public List<SequenceItem> getItems() {
        return items;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItems(List<SequenceItem> items) {
        this.items = items;
    }
}
