package com.example.assignment;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MusicBrainzApiService {

    @GET("ws/2/artist/")
    Call<ArtistResponse> searchArtist(
            @Query("query") String query,
            @Query("fmt") String format,
            @Query("limit") int limit
    );


    @GET("ws/2/artist/{mbid}")
    Call<Artist> getArtistDetails(
            @Path("mbid") String mbid,
            @Query("fmt") String format,
            @Query("inc") String includes
    );
}