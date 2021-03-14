package de.craftedcrime.bungee.servermanager.http;
/*
 * Created by ian on 11.03.21
 * Location: de.craftedcrime.bungee.servermanager.http
 * Created for the project servermanager with the name StatsClient
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.http.HttpClient;
import java.util.logging.Logger;

public class StatsClient {

    private String BASE_URL = "http://localhost:8080";

    public StatsService statsService;

    private Logger logger = Logger.getLogger(HttpClient.class.getName());

    private StatsService initHttpService(String baseUrl) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request.Builder ongoing = chain.request().newBuilder();
                    ongoing.addHeader("Accept", "application/json;versions=1");
                    ongoing.addHeader("Accept", "text/html");
                    return chain.proceed(ongoing.build());
                })
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        logger.info("Registered Stats-Http-Client for further usage.");
        return retrofit.create(StatsService.class);
    }

}
