package com.example.assignment;

//Import necessary OkHttp classes for intercepting network requests.
import java.io.IOException;//For handling input/output exceptions that might occur
//during network operations.

import okhttp3.Interceptor;// The core interface for intercepting newtwork requests.

import okhttp3.Request;//Represents an HTTP Request.
import okhttp3.Response;//Represents an HTTP Response.




/**
        * A custom OkHttp {@link Interceptor} that adds a "User-Agent" header to every outgoing HTTP request.
        * Many web services, including MusicBrainz, prefer or require a User-Agent header
 * to identify the client making the request. This helps in tracking usage and
 * can sometimes be crucial for avoiding rate limiting or getting proper responses.
 */
public class UserAgentInterceptor implements Interceptor {
    //The User-Agent string that will be added to the request headers.
    private final String userAgent;

    /*
    * Constructs a new UserAgentInterceptor
    * The userAgent parameter us the custom User-Agent string to use
    * */
    public UserAgentInterceptor(String userAgent) {
        this.userAgent = userAgent;
    }


    /*
    * Intercepts an HTTP requests and adds the custom User-Agent header.
    * This method is called OkHttp for every request in the chain.
    *
    * The chain of interceptros "chain", allows allows the user to proceed with the request.
    * The Response return type is the responce from the next interceptor in the chain, or the network.
    * If an I/O error occurs during the requets then it throws an IOExeption.
    * */
    @Override
    public Response intercept(Chain chain) throws IOException {
        //Creates a new request builder based on the original request.
        Request request = chain.request()
                .newBuilder()
                //the "User-Agent" header is replaced with the custom string.
                .header("User-Agent", userAgent)
                .build();
        //Proceed with the modified request through the rest of the interceptor chain
        //and eventually to the network.
        return chain.proceed(request);
    }
}