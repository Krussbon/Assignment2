package com.example.assignment;



import java.util.List;

public class Artist {
    private String id;
    private String name;
    private String disambiguation; // This often contains interesting facts
    private List<Tag> tags;

    // Getters
    public String getName() { return name; }
    public String getDisambiguation() { return disambiguation; }
    public List<Tag> getTags() { return tags; }
}