package com.example.assignment;
//Import the List Interface from Java's utility library
import java.util.List;

//This class represets the structure of a response from the MusicBrainz
// API that returns a list of artists
//This class is designed to match the JSON structure of the API.
public class ArtistResponse {
    //Private member available to hold a list of Artist objects.
    //The name 'artists' here must typically match the key name in the JSON
    //response from the API for automatic parsing by libraries like Gson
    //or Retrofit
    private List<Artist> artists;

    //This is the getter methods that allows access to the list of artists from outside this class.
    //This is how other parts of the application will retrieve artist data.
    public List<Artist> getArtists() {
        return artists;
    }
}
