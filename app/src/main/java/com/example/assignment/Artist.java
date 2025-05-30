package com.example.assignment;



import java.util.List;

//This represents an Artist that is retrieved using the MusicBrainz API
public class Artist {

    //this contains the MBID of the artist in the MusicBrainz API
    private String id;
    //This contains the name of the artist
    private String name;
    //This is any additional text that helps distinguished this artist from others that sound identical
    //or similar
    private String disambiguation; // This often contains interesting facts
    //A list of Tag objects associated with the artist
    private List<Tag> tags;

    // Getters
    //These methods provide controlled access to the private member variables from outside this class
    //they are named following the JavaBean convention (getFieldName().)
    //Returns the name
    public String getName() { return name; }
    //Returns the dismbiguation for the artist
    public String getDisambiguation() { return disambiguation; }
    //Returns a list of tag objects associatied with the artist
    public List<Tag> getTags() { return tags; }


}