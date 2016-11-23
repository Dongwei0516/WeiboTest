package com.dongwei.weibotest3.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dongwei on 2016/11/9.
 */

public class HttpUtils {

    public static InputStream getInputStream(String url) {
        URL home_url = null;

        InputStream input = null;

        HttpURLConnection conn = null;

        try {
            home_url = new URL(url);

            conn = (HttpURLConnection) home_url.openConnection();

            conn.setRequestMethod("GET");

            conn.setConnectTimeout(5000);

            conn.connect();

            input = conn.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return input;

    }

    public static String getStringByStream(String url) {

        StringBuilder sb = null;
        BufferedReader br = null;

        InputStream input = getInputStream(url);


        if (input != null) {
            br = new BufferedReader(new InputStreamReader(input));

            String line = null;
            sb = new StringBuilder();
            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            sb = new StringBuilder("");
        }

        return sb.toString();

    }
}
