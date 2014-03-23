package com.orb.arma.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.orb.arma.R;

import java.util.Comparator;
import java.util.List;
import com.orb.arma.beans.Server;

public class ServerListAdapter extends ArrayAdapter<Server> {
    private Activity context;
    private List<Server> servers;
    private Server selectedServer;

    public List<Server> getServers() {
        return servers;
    }

    public void setSelectedServer(Server selectedServer) {
        this.selectedServer = selectedServer;
    }

    public ServerListAdapter(Activity context, List<Server> servers) {
        super(context, R.layout.server_list_item, servers);
        this.context = context;
        this.servers = servers;
    }

    /*private view holder class, so we can cache the views*/
    private class ViewHolder {
        TextView tvName;
        TextView tvCountry;
        ImageView tvPasword;
        TextView tvPlayers;
        TextView tvState;
    }

    public Server getItem(int position) {
        return servers.get(position);
    }

    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (view == null) {
            view = inflater.inflate(R.layout.server_list_item, null);

            holder = new ViewHolder();
            if (view != null) {
                //get the views (saveHistory them in holder class)
                holder.tvName = (TextView) view.findViewById(R.id.server_name);
                holder.tvCountry = (TextView) view.findViewById(R.id.server_country);
                holder.tvPasword = (ImageView) view.findViewById(R.id.server_password);
                holder.tvPlayers = (TextView) view.findViewById(R.id.server_player_count);
                holder.tvState = (TextView) view.findViewById(R.id.server_state);
                view.setTag(holder);
            }
        }

        //now we can use the cached views
        holder = (ViewHolder) view.getTag();

        Server server = getItem(position);

        holder.tvName.setText(server.getName());
        holder.tvCountry.setText(server.getCountry());
        holder.tvPlayers.setText(server.getPlayersString());
        holder.tvState.setText(server.getState());

        //show password lock image, if server has a password
        if(server.getPassword()){
            holder.tvPasword.setImageResource(R.drawable.lock);
            holder.tvPasword.setVisibility(View.VISIBLE);
        }

        // change text color if server has friends
        if(server.hasFriends){
            holder.tvName.setTextColor(context.getResources().getColor(R.color.red_purple));
        }else{
            holder.tvName.setTextColor(context.getResources().getColor(R.color.blackish));
        }

        // set selected row (use the padding to use background as border)
        if(server.equals(selectedServer)){
            view.setPadding(0,4,0,4);
        }else{
            view.setPadding(0,0,0,0);
        }

        return view;
    }


    //sort on name
    public static Comparator<Server> sortNameAsc = new Comparator<Server>(){
        @Override
        public int compare(Server e1, Server e2) {
            //ignore case: so its a,B,c instead of capital letters first
            return (e1.getName().compareToIgnoreCase(e2.getName()));
        }
    };
    public static Comparator<Server> sortNameDesc = new Comparator<Server>(){
        @Override
        public int compare(Server e1, Server e2) {
            return (e2.getName().compareToIgnoreCase(e1.getName()));
        }
    };


    //sort on Player count (int)
    public static Comparator<Server> sortPlayersAsc = new Comparator<Server>(){
        @Override
        public int compare(Server e1, Server e2) {
            return e1.getPlayers() - e2.getPlayers();
        }
    };
    public static Comparator<Server> sortPlayersDesc = new Comparator<Server>(){
        @Override
        public int compare(Server e1, Server e2) {
            return e2.getPlayers() - e1.getPlayers();
        }
    };


    //sort on country
    public static Comparator<Server> sortCountryAsc = new Comparator<Server>(){
        @Override
        public int compare(Server e1, Server e2) {
            return (e1.getCountry().compareTo(e2.getCountry()));
        }
    };
    public static Comparator<Server> sortCountryDesc = new Comparator<Server>(){
        @Override
        public int compare(Server e1, Server e2) {
            return (e2.getCountry().compareTo(e1.getCountry()));
        }
    };

    //sort on status
    public static Comparator<Server> sortStateAsc = new Comparator<Server>(){
        @Override
        public int compare(Server e1, Server e2) {
            return (e1.getState().compareTo(e2.getState()));
        }
    };
    public static Comparator<Server> sortStateDesc = new Comparator<Server>(){
        @Override
        public int compare(Server e1, Server e2) {
            return (e2.getState().compareTo(e1.getState()));
        }
    };


    //sort on has friends (friends on top)
    public static Comparator<Server> sortFriends = new Comparator<Server>(){
        @Override
        public int compare(Server e1, Server e2) {
            return (e2.hasFriends.compareTo(e1.hasFriends));
        }
    };

}