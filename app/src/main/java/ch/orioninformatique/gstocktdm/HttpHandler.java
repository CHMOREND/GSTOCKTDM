package ch.orioninformatique.gstocktdm;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpHandler {
    private static  final String TAG = HttpHandler.class.getSimpleName();
    public HttpHandler(){

    }
    public String makeServiceCall(String reqUrl){
        String response = null;
        try {
            URL url = new URL(reqUrl);
            //HttpURLConnection conn = (HttpsURLConnection) url.openConnection();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // lecteur de la réponse
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response =  convertStreamToString(in);

        } catch (MalformedURLException e){
            Log.e(TAG, "Erreur format du URL: "+e.getMessage());

        } catch (ProtocolException e){
            Log.e(TAG, "Protocole exception: "+e.getMessage());

        } catch (IOException e) {
            Log.e(TAG, "IO Exception: "+e.getMessage());

        } catch (Exception e){
            Log.e(TAG, "Exception: "+e.getMessage());

        }
        return response;
    }

    private String convertStreamToString(InputStream is){
        BufferedReader reader =new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while  ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
