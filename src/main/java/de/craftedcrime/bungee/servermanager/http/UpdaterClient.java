package de.craftedcrime.bungee.servermanager.http;
/*
 * Created by ian on 07.04.21
 * Location: de.craftedcrime.bungee.servermanager.http
 * Created for the project servermanager with the name UpdaterClient
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.http.HttpClient;
import java.util.logging.Logger;

public class UpdaterClient {

    private String BASE_URL = "http://nexus.ianfd.de";

    public UpdaterService updaterService;

    public UpdaterClient() {
        initHttpService(BASE_URL);
    }

    private Logger logger = Logger.getLogger(HttpClient.class.getName());

    private UpdaterService initHttpService(String baseUrl) {
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
        logger.info("Registered Update-Http-Client for further usage.");
        return retrofit.create(UpdaterService.class);
    }

}
