package fbvd.ma7moud3ly.com;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyRequests {

    public static boolean isConnected(Context c) {
        try {
            ConnectivityManager connectivityManager =(ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    // HTTP GET request
    public String sendGet(String url) {
        URL obj;
        try {
            obj = new URL(url);
        } catch (Exception e) {
            return e.toString();
        }
        StringBuffer response;
        try {
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla Firefox");

            int responseCode = con.getResponseCode();
            int status = con.getResponseCode();
            BufferedInputStream in;
            if (status >= 400) {
                in = new BufferedInputStream(con.getErrorStream());
            } else {
                in = new BufferedInputStream(con.getInputStream());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String inputLine;
            response = new StringBuffer();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}




