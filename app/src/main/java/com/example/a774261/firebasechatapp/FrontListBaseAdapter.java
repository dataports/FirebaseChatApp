package com.example.a774261.firebasechatapp;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FrontListBaseAdapter extends BaseAdapter {
    private static ArrayList<FrontDetails> itemDetailsrrayList;

    private LayoutInflater l_Inflater;

    public FrontListBaseAdapter(Context context, ArrayList<FrontDetails> results) {
        itemDetailsrrayList = results;
        l_Inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return itemDetailsrrayList.size();
    }

    public Object getItem(int position) {
        return itemDetailsrrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // get the views in frontview xml file where you have
    // define multiple views that will appear in listview each row
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.frontview, null);
            holder = new ViewHolder();
            holder.Image = (ImageView) convertView.findViewById(R.id.adminpic1);
            holder.MsgType = (TextView) convertView.findViewById(R.id.msgtype1);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.Image.setImageResource(R.drawable.mainlogo); // you can set your setter here
        holder.MsgType.setText(itemDetailsrrayList.get(position).getMsgType());

        return convertView;
    }

    // holder view for views
    static class ViewHolder {
        ImageView Image;
        TextView MsgType;
    }
}
