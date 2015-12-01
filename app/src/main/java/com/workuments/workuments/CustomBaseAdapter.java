package com.workuments.workuments;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bradcollins on 11/19/15.
 */
public class CustomBaseAdapter extends BaseAdapter   {

    private static ArrayList<SiteResults> searchArrayList;

    private LayoutInflater mInflater;

    public CustomBaseAdapter(Context context, ArrayList<SiteResults> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_site_row_view, null);
            holder = new ViewHolder();
            holder.txtSiteName = (TextView) convertView.findViewById(R.id.siteNameTextView);
            holder.txtSiteUrl = (TextView) convertView.findViewById(R.id.siteUrlTextView);
            holder.ivSiteIcon = (ImageView) convertView.findViewById(R.id.imageView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        byte[] img = searchArrayList.get(position).getIcon();
        if(img != null)
            holder.ivSiteIcon.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
        holder.txtSiteName.setText(searchArrayList.get(position).getName());
        holder.txtSiteUrl.setText(searchArrayList.get(position).getUrl());

        return convertView;
    }

    static class ViewHolder {
        ImageView ivSiteIcon;
        TextView txtSiteName;
        TextView txtSiteUrl;
    }
}

