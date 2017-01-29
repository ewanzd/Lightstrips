package com.ewanzd.lightstrips;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a sequence of light. Its possible to give him a name.
 */
public class Sequence {

    private long id;
    private String name;
    private List<SequenceItem> items = new ArrayList<>();

    public Sequence() {
    }

    public Sequence(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SequenceItem> getItems() {
        return items;
    }

    public void setItems(List<SequenceItem> items) {
        this.items = items;
    }
}
