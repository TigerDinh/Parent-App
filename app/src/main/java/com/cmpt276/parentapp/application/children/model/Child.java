package com.cmpt276.parentapp.application.children.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Child implements Serializable {

    private final String name;
    private final String imageLocation;
    private final Integer childId;

    @NonNull
    @Override
    public String toString() {
        return
                "name='" + name;

    }

    public Child(String name, String imageLocation, Integer childID) {
        this.name = name;
        this.imageLocation = imageLocation;
        this.childId = childID;
    }

    public Integer getChildId() {
        return childId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Child)) return false;
        Child child = (Child) o;
        return name.equals(child.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getImageLocation()
    {
        return imageLocation;
    }

}
