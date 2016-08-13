package com.example.home.sqlitedemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by home on 8/13/2016.
 */

public class CustomAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Contact> Items;

    public CustomAdapter(Activity activity, List<Contact> Items) {
        this.activity = activity;
        this.Items = Items;
    }

    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public Object getItem(int location) {
        return Items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);
        TextView title = (TextView) convertView.findViewById(R.id.tvData);
        TextView mobile = (TextView) convertView.findViewById(R.id.tvMobile);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        // getting movie data for the row
        Contact m = Items.get(position);
        // title
        title.setText(m.getName());
        mobile.setText(m.getPhoneNumber());

        //convert byte to bitmap take from contact class
        byte[] outImage = m.get_image();
        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
        imageView.setImageBitmap(theImage);


        return convertView;
    }

}
