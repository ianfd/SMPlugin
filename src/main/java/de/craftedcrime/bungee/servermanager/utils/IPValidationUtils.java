package de.craftedcrime.bungee.servermanager.utils;
/*
 * Created by ian on 02.03.21
 * Location: de.craftedcrime.bungee.servermanager.utils
 * Created for the project servermanager with the name IPValidationUtils
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPValidationUtils {

    private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    private static final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);

    public boolean isValidInet4Address(String ip) {
        if (ip == null) {
            return false;
        }

        Matcher matcher = IPv4_PATTERN.matcher(ip);

        return matcher.matches();
    }

    public IPValidationUtils() {
    }
}
