package com.example.googlebooks;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class Loader extends AsyncTaskLoader< ArrayList<Books> > {

    private String urlString;
    public Loader(@NonNull Context context, String urlString) {
        super(context);
        this.urlString = urlString;
    }

    @Nullable
    @Override
    public ArrayList<Books> loadInBackground() {

        URL url = getURL();
        ArrayList<Books> arrayList = null;

        Log.v("URL", url.toString());
        try {
            String jsonData = getData(url);
            arrayList = extractJSONData(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    private URL getURL() {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private String getData(URL url) throws IOException {
        String data = "";

        if(url == null)
            return data;

        HttpsURLConnection httpsURLConnection = null;
        InputStream inputStream = null;

        try {
            httpsURLConnection = (HttpsURLConnection)url.openConnection();
            httpsURLConnection.setConnectTimeout(15000);
            httpsURLConnection.setReadTimeout(10000);
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.connect();

            if(httpsURLConnection.getResponseCode() == 200) {
                inputStream = httpsURLConnection.getInputStream();
                data = readFromStream(inputStream);
            }

            else
            {
                Log.v("Loader","Connection Error "+httpsURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(httpsURLConnection!=null)
            httpsURLConnection.disconnect();

        if(inputStream!=null)
            inputStream.close();

        return data ;
    }

    private String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = null;

        if(inputStream != null ){
            inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();

            while(line!=null)
            {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }

        return stringBuilder.toString();
    }

    ArrayList<Books> extractJSONData(String jsonSring) {
        ArrayList<Books> arrayList = null;

        if(jsonSring == null || jsonSring.isEmpty())
            return arrayList;

        try {
            arrayList = new ArrayList<Books>();

            JSONObject jsonObject = new JSONObject(jsonSring);
            JSONArray jsonArray = jsonObject.optJSONArray("items");

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject temp = jsonArray.getJSONObject(i).getJSONObject("volumeInfo");
                String title = temp.optString("title");
                String url = temp.optString("infoLink");

                Log.v("Book",title + " " + url );

                arrayList.add(new Books(title,url));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;

    }
}
