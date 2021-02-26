package de.craftedcrime.bungee.servermanager.utils;
/*
 * Created by ian on 25.02.21
 * Location: de.craftedcrime.bungee.servermanager.servermanager.utils
 * Created for the project servermanager with the name ConfigUtils
 */

import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigUtils {

    public void saveDefaultConfig(String path) throws IOException {
        InputStream inputStream = Class.class.getResourceAsStream("/resources/config.yml");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        if (inputStreamReader != null) {
            YamlConfiguration configuration;
        }

    }


}
