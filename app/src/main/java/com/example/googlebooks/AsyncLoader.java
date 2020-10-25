package com.example.googlebooks;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class AsyncLoader extends AsyncTaskLoader< ArrayList<Books> > {

    private  String requestUrl;

    public AsyncLoader(@NonNull Context context, String requestUrl) {
        super(context);
        this.requestUrl = requestUrl;
    }

    @NonNull
    @Override
    public ArrayList<Books> loadInBackground() {

        ArrayList<Books> arrayList = null;

        URL url = getURL();

        Log.v("URL", url.toString());

        try {
            String data = makeRequest(url);
            arrayList = jsonData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    private String makeRequest(URL url) throws IOException {
        String data = "";
        HttpsURLConnection httpsURLConnection = null;
        InputStream inputStream = null;

        if(url == null)
            return data;

        try {
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setConnectTimeout(15000);
            httpsURLConnection.setReadTimeout(10000);
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.connect();

            if(httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = url.openStream();
                data = readFromStream(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(httpsURLConnection != null)
            httpsURLConnection.disconnect();

        if (inputStream != null) {
            inputStream.close();
        }

        return data;
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

    private URL getURL() {
        URL url = null;

        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    ArrayList<Books> jsonData(String jsonSring) {
        ArrayList<Books> arrayList = null;

        if(jsonSring == null || jsonSring.isEmpty())
            return arrayList;

        try {
            JSONObject jsonObject = new JSONObject(jsonSring);
            JSONArray jsonArray = jsonObject.optJSONArray("items");

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject temp = jsonArray.getJSONObject(i).getJSONObject("volumeInfo");
                String title = temp.optString("title");
                String author = temp.getJSONArray("authors").getString(0);
                String url = temp.optString("infoLink");

                Log.v("Book",title + " " + author + " " + url );

                arrayList.add(new Books(title,author,url));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;

    }
}
