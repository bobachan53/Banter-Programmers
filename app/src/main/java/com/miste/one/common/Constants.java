package com.miste.one.common;

import java.util.Random;

public class Constants {


//  public static String API_URL = "https://ezq-api.herokuapp.com/api/";
    public static String API_URL = "http://192.168.1.102:8001";
//  public static String API_URL = "http://192.168.43.155:8001";

    public static String IMAGE_URL = API_URL+"/storage/";

    static public String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    static public String getSaltInt() {
        String SALTCHARS = "1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

        public static String UNIQUE_ID_FOR_BOOKFOROTHERS = "http://192.168.1.102:8001";
}
