package com.example.googlebooks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Books> {
    public CustomAdapter(@NonNull Context context, ArrayList<Books> arrayList ) {
        super(context, 0 ,arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);

        TextView textView1,textView2;

        textView1 = (TextView)listItem.findViewById(R.id.title);
        textView2 = (TextView)listItem.findViewById(R.id.author);

        textView1.setText(getItem(position).getAuthor());
        textView2.setText(getItem(position).getTitle());

        return listItem;
    }
}
