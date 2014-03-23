package com.orb.arma.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.orb.arma.R;

import java.util.ArrayList;
import java.util.List;

import com.orb.arma.beans.PlayerDetail;

public class PlayerListAdapter extends ArrayAdapter<PlayerDetail> implements Filterable {
    private Activity context;
    private List<PlayerDetail> players;
    private boolean hasFriends = false;

    public PlayerListAdapter(Activity context, List<PlayerDetail> players, boolean hasFriends) {
        super(context, R.layout.players_list_item, players);
        this.context = context;
        this.players = players;
        this.hasFriends = hasFriends;
    }

    /*private view holder class, so we can cache the views*/
    private class ViewHolder {
        TextView tvName;
        TextView tvScore;
        TextView tvDeaths;
    }

    public PlayerDetail getItem(int position) {
        return players.get(position);
    }

    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (view == null) {
            view = inflater.inflate(R.layout.players_list_item, null);

            holder = new ViewHolder();
            if (view != null) {
                //get the views (saveHistory them in holder class)
                holder.tvName = (TextView) view.findViewById(R.id.player_name);
                holder.tvScore = (TextView) view.findViewById(R.id.player_score);
                holder.tvDeaths = (TextView) view.findViewById(R.id.player_deaths);
                view.setTag(holder);
            }
        }

        //now we can use the cached views
        holder = (ViewHolder) view.getTag();

        PlayerDetail player = getItem(position);

        if(player != null){
            holder.tvName.setText(player.name);
            holder.tvScore.setText(player.score);
            holder.tvDeaths.setText(player.deaths);

            if(hasFriends){
                if(player.isFriend){
                    holder.tvName.setTextColor(context.getResources().getColor(R.color.red_purple));
                }else{
                    holder.tvName.setTextColor(context.getResources().getColor(R.color.blackish));
                }
            }
        }

        return view;
    }


    /* !!! We only keep track of the active players, so this filter is no longer needed
    private ItemFilter mFilter = new ItemFilter();

    // override the getFilter and return the custom filter
    public Filter getFilter() {
        return mFilter;
    }

    //Custom Filter implementation for the items adapter.
    private class ItemFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //Log.d("test", "playerList filtering: " + constraint  );
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            final List<PlayerDetail> orgList = players;
            final ArrayList<PlayerDetail> newList = new ArrayList<PlayerDetail>();

            //Filter on player active players
            if(filterString.equals("active")){
                for (PlayerDetail player : orgList) {
                    if (player.active) {
                        newList.add(player);
                    }
                }
            }

            results.values = newList;
            results.count = newList.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void publishResults(CharSequence constraint, FilterResults results) {
            //filteredData = (ArrayList<PlayerDetail>) results.values;
            //notifyDataSetChanged();

            // this destroys the original data
            players.clear();
            players.addAll((ArrayList<PlayerDetail>)results.values);
            notifyDataSetChanged();
        }
    }
    */
}



