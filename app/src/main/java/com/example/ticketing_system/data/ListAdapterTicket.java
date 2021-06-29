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


public class ListAdapterTicket extends ArrayAdapter<Fixture> {


    private Context mContext;
    private int mResource;


    private static class ViewHolder {
        TextView home;
        TextView away;
        TextView time;
        TextView bought;
        TextView id;
        ImageView home_logo;
        ImageView away_logo;
    }


    public ListAdapterTicket(Context context, int resource, ArrayList<Fixture> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the fixture information
        String home = getItem(position).getHome();
        String away = getItem(position).getAway();
        String created_at = getItem(position).getCreated_at();
        String ticket_id = getItem(position).getTicket_id();
        String time = getItem(position).getTime();
        // Logo URL's
        String home_logo = getItem(position).getHome_logo();
        String away_logo = getItem(position).getAway_logo();

        //Create the fixture object with the information
        Fixture fixture = new Fixture(home,away,home_logo,away_logo,created_at,ticket_id,time);


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
            holder.time = (TextView) convertView.findViewById(R.id.time_item);
            // Home team logo image view
            holder.home_logo =  convertView.findViewById(R.id.home_logo);
            // Away team logo image view
            holder.away_logo =  convertView.findViewById(R.id.away_logo);
            // When is bought
            holder.bought = convertView.findViewById(R.id.created_at);
            // Ticket id
            holder.id = convertView.findViewById(R.id.ticket_id);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.home.setText(fixture.getHome());
        holder.away.setText(fixture.getAway());
        holder.time.setText(fixture.getTime());
        holder.bought.setText("BOUGHT " + fixture.getCreated_at());
        holder.id.setText("#"+fixture.getTicket_id());

        // Loading image from URL
        Picasso.get().load(home_logo).into(holder.home_logo);
        Picasso.get().load(away_logo).into(holder.away_logo);

        return convertView;
    }
}
