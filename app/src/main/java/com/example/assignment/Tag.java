package com.example.assignment;
// This class represents a 'Tag' object, typically used to categorize or describe entities
// within the MusicBrainz database (e.g., genres, styles, or descriptive keywords).
// It follows the POJO (Plain Old Java Object) pattern, which means it's a simple
// class primarily holding data, with private fields and public getter methods for access.
public class Tag {
    //The name of the tag (e.g. "rock", "jazz", "singer-songwriter")
    private String name;
    //The count associated with the tag, indicating
    //its prevalence or how many times it has been applied
    private int count;
/*
* Gets the name of the tag as a String.
* */
    public String getName() { return name; }

    /**
     * Gets the count associated with the tag.
     * @return The count as an integer.
     */
    public int getCount() { return count; }



}