package de.craftedcrime.bungee.servermanager.utils;
/*
 * Created by ian on 03.03.21
 * Location: de.craftedcrime.bungee.servermanager.utils
 * Created for the project servermanager with the name GeneralUtils
 */

import java.util.regex.Pattern;

public class GeneralUtils {

    public GeneralUtils() {
    }

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }
}
