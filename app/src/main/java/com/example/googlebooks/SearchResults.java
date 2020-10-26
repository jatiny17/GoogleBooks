package com.example.googlebooks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity implements LoaderManager.LoaderCallbacks< ArrayList<Books> >{

    private CustomAdapter customAdapter;
    private ListView listView;
    private Uri builtURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        listView = (ListView)findViewById(R.id.listView);
        customAdapter = new CustomAdapter(this,new ArrayList<Books>());
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(customAdapter.getItem(i).getUrl()));
                startActivity(browserIntent);
            }
        });

        String temp = getIntent().getStringExtra("key");

        String API_KEY = "AIzaSyBoRRVQCcy3y_bw23kvfxPiKD-fV_izDOs";

        Log.v("Key", temp);

        String BOOK_BASE_URL =
                "https://www.googleapis.com/books/v1/volumes?";

        String QUERY_PARAM = "q";

        builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, temp)
                .appendQueryParameter("orderBy","relevance")
                .appendQueryParameter("Key", API_KEY)
                .build();

        Log.v("Https", builtURI.toString());

        getSupportLoaderManager().initLoader(0,null,SearchResults.this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<ArrayList<Books> > onCreateLoader(int id, @Nullable Bundle args) {
        return new com.example.googlebooks.Loader(this,builtURI.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Books>> loader, ArrayList<Books> data) {
        customAdapter.clear();

        if(data == null)
            return;

        customAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Books>> loader) {
        customAdapter.clear();
    }
}
