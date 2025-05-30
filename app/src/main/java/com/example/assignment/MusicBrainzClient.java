package com.example.assignment;

// Import necessary OkHttp and Retrofit classes.
import okhttp3.OkHttpClient; //An http client for making network requets.
import okhttp3.logging.HttpLoggingInterceptor;// An okHttp interceptor for logging HTTP request/response data.
import retrofit2.Retrofit;//The main class for building and configuring retrofit.
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton client for interacting with the MusicBrainz API using Retrofit.
 * This class ensures that only one instance of Retrofit and its associated services
 * is created and reused throughout the application, which is efficient for network operations.
 */
public class MusicBrainzClient {

    //All api calls will be relative to this URL
    private static final String BASE_URL = "https://musicbrainz.org/";
    //A static REtrofit instance. It's initialised once and reused.
    private static Retrofit retrofit = null;

    /**
     * Provides a singleton instance of the MusicBrainzApiService.
     * If the Retrofit client has not been initialized yet, it builds it.
     * This method ensures that network client setup is performed only once.
     *
     * @return An instance of {@link MusicBrainzApiService} for making API calls.
     */
    public static MusicBrainzApiService getClient() {
        //Check if the Retrofit instance has not been created yer
        if (retrofit == null) {
            //Create an HttpLoggingInterceprot to log network requests and responses.
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //The logging level is set to BASIC, which logs requests and response lines.
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

            //Build an OkHttpClient instance. OkHttp is the HTTP client Retrofit uses.
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    //Add a custom interceptor to include User-agent header in requests.
                    //This is often required by APIs for identification and rate limiting.
                    .addInterceptor(new UserAgentInterceptor("Assignment/1.0"))
                    .build();
            //REtrofit instance is built
            retrofit = new Retrofit.Builder()
                    //the base url is set for all api requests
                    .baseUrl(BASE_URL)
                    //Set the custom OkHttpClient to be used by Retrofit.
                    .client(client)
                    //A converter factory is added to automatically convert JSON responses into
                    //Java Objects and vice versa
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        //Create and return an implementation of the MusicBrainsAPIService interface.
        //REtrofit dynamically generates the cod efor this interface based on its annotations.
        return retrofit.create(MusicBrainzApiService.class);
    }
}
