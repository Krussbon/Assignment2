package com.example.assignment;

import retrofit2.Call; //REpresents a single API Request.
import retrofit2.http.GET;//HTTP GET Requests
import retrofit2.http.Path;// Annotation for dynamic parts of a url path
import retrofit2.http.Query;// Annotation for query parameters in a URL.

//Import necessary retrofit classes for defining API interfaces

/*
* Retrofit interface for interacting with the MusicBrainz API.
* This interface defines the structure of the API endpoints your app will call,
* including the HTTP method, URL paths, and query parameters.
* */
public interface MusicBrainzApiService {

    /*
    * Defines an API call to earch for artists.
    *
    * The results wrapped in {@ling Artist response} object.
    *
    * the query parameter searches for different types and tags (e.g. type:person and tag:pop).
    * the format parameter is the desired format (in this case it excepts "json" format).
    * limit the maximum number of results to return.
    * A {@Link Call} Object that can be execudet to get the {@Link ArtistResponse}.
    * */

    @GET("ws/2/artist/")
    Call<ArtistResponse> searchArtist(
            //A get requests is specified and send to the
            // /ws/2/artist/ endpoint.
            @Query("query") String query, //Maps the query parameter to the URL query string (e.g. ?query=..)
            @Query("fmt") String format, //Maps the fmt parameter to the URL query string (e.g. ?fmt=json).
            @Query("limit") int limit//Maps the limit parameter to the URL query string (e.g. ?limit=100).
    );

    /**
     * Defines an API call to get detailed information about a specific artist by their MusicBrainz ID.
     * The result is a single {@link Artist} object.
     *
     * @param mbid The MusicBrainz ID (MBID) of the artist. This is a unique identifier.
     * @param format The desired response format (e.g., "json").
     * @param includes Additional data to include in the response (e.g., "tags", "aliases").
     * @return A {@link Call} object that can be executed to get the {@link Artist} details.
     */
    @GET("ws/2/artist/{mbid}")// Specifies a GET request where {mbid} is a placeholder in the URL path.
    Call<Artist> getArtistDetails(
            @Path("mbid") String mbid,// Replaces the {mbid} placeholder in the URL path with the value of 'mbid'.
            @Query("fmt") String format,// Maps the 'fmt' parameter to the URL query string.
            @Query("inc") String includes// Maps the 'inc' parameter to the URL query string (e.g., &inc=tags).
    );
}