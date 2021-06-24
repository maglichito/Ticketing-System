package com.example.ticketing_system.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ticketing_system.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ListAdapter extends ArrayAdapter<Fixture> {


    private Context mContext;
    private int mResource;


    private static class ViewHolder {
        TextView home;
        TextView away;
        TextView time;
        ImageView home_logo;
        ImageView away_logo;
    }


    public ListAdapter(Context context, int resource, ArrayList<Fixture> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the fixture information
        String home = getItem(position).getHome();
        String away = getItem(position).getAway();
        String time = getItem(position).getTime();
        String date = getItem(position).getDate();
        // Logo URL's
        String home_logo = getItem(position).getHome_logo();
        String away_logo = getItem(position).getAway_logo();

        //Create the fixture object with the information
        Fixture fixture = new Fixture(home,away,date,time,home_logo,away_logo);


        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            // Home team text
            holder.home = (TextView) convertView.findViewById(R.id.home_team);
            // Away team text
            holder.away = (TextView) convertView.findViewById(R.id.away_team);
            // Time text
            holder.time = (TextView) convertView.findViewById(R.id.time_item);
            // Home team logo image view
            holder.home_logo =  convertView.findViewById(R.id.home_logo);
            // Away team logo image view
            holder.away_logo =  convertView.findViewById(R.id.away_logo);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.home.setText(fixture.getHome());
        holder.away.setText(fixture.getAway());
        holder.time.setText(fixture.getTime());

        // Loading image from URL
        Picasso.get().load(home_logo).into(holder.home_logo);
        Picasso.get().load(away_logo).into(holder.away_logo);

        return convertView;
    }
}
