package de.craftedcrime.bungee.servermanager.http;
/*
 * Created by ian on 07.04.21
 * Location: de.craftedcrime.bungee.servermanager.http
 * Created for the project servermanager with the name UpdaterService
 */

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.io.File;

public interface UpdaterService {

    @GET("/de/craftedcrime/bungee/servermanager/servermanager/latest")
    Call<String> getNewestVersion();

    @GET("/de/craftedcrime/bungee/servermanager/servermanager/{version}/servermanager-{version}.jar")
    Call<File> getNewestJar(@Path("version") String version);

}
