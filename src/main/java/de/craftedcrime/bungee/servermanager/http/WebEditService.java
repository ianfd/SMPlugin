package de.craftedcrime.bungee.servermanager.http;
/*
 * Created by ian on 04.04.21
 * Location: de.craftedcrime.bungee.servermanager.http
 * Created for the project servermanager with the name WebEditService
 */

import de.craftedcrime.infrastructure.servermanager.middleware.ConfigEdit;
import de.craftedcrime.infrastructure.servermanager.middleware.ConfigUpload;
import de.craftedcrime.infrastructure.servermanager.middleware.KeySecretPair;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WebEditService {

    @GET("/edit/getserver")
    Call<ConfigEdit> downloadConfigEdit(@Query("key") String key, @Query("secret") String secret);

    @POST("/generatekey")
    Call<KeySecretPair> startEditing(@Body ConfigUpload configUpload);

}
