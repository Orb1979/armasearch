package com.orb.arma.adapters;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.orb.arma.R;
import com.orb.arma.beans.Search;
import java.util.ArrayList;
import java.util.Collections;


public class HistoryAdapter extends ArrayAdapter<Search> {
    private Activity context;
    private ArrayList<Search> history;

    public ArrayList<Search> getHistory() {
        return history;
    }

    public HistoryAdapter(Activity context, ArrayList<Search> history) {
        super(context, R.layout.server_list_item, history);
        this.context = context;
        this.history = history;
        loadHistory(context);
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView tvName;
        TextView tvPlayers;
        TextView tvCountry;
        TextView tvDedicated;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.history_list_item, null);
            holder = new ViewHolder();
            holder.tvName      = (TextView) convertView.findViewById(R.id.searchName);
            holder.tvPlayers   = (TextView) convertView.findViewById(R.id.searchPlayerCount);
            holder.tvCountry   = (TextView) convertView.findViewById(R.id.searchCountry);
            holder.tvDedicated = (TextView) convertView.findViewById(R.id.searchDedicated);

            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        Search search = getItem(position);
        holder.tvName.setText(search.getName());
        holder.tvPlayers.setText(""+search.getPlayerCount());
        holder.tvCountry.setText(""+search.getCountry());
        holder.tvDedicated.setText(""+search.getDedicatedOnly().toString());

        return convertView;
    }


    public void save(Search currentSearch, int historyIndex){
        // a new search
        if(historyIndex == -1){

            //Check for duplicate
            int duplicate = -1;
            Search oldSearch;

            for(int i=0;i<history.size();i++){

                oldSearch = history.get(i);

                //we already have a search with the same name
                if(currentSearch.getName().equals(oldSearch.getName()))
                    duplicate = i;

                //if one of these values are different, then its no duplicate
                if(currentSearch.getPlayerCount() != oldSearch.getPlayerCount())
                    duplicate = -1;

                if(!currentSearch.getCountry().equals(oldSearch.getCountry()))
                    duplicate = -1;

                if(currentSearch.getDedicatedOnly() != (oldSearch.getDedicatedOnly()))
                    duplicate = -1;

                // if we found a duplicate, we need to break because we could find
                // another duplicate name which does have different other values
                if(duplicate != -1){
                    break;
                }
            }

            //not duplicate
            if(duplicate == -1){
                //always save the latest value at the beginning and shift the rest
                history.add(0, currentSearch);

            }else{
                setItemOnTop(duplicate);
            }
        }
        //a search from the history list
        else{
            setItemOnTop(historyIndex);
        }

        //saveHistory to sharedPref (also if duplicate because the order is changed)
        SharedPrefAdapter.saveHistory(context, history);

        //update the list
        notifyDataSetChanged();
    }

    private void setItemOnTop(int duplicateIndex){
        // set the search on top of the list
        Collections.swap(history, duplicateIndex, 0);

    }

    public void loadHistory(Context context){
        // load history from shared preferences
        // (called on startup and also when the sharedPreferences are deleted)
        history.clear();
        SharedPrefAdapter.loadHistory(context, history);
        save(new Search("[tacBF]", 0, "", false), -1);
    }

    public void updateHistory(){
        notifyDataSetChanged();
    }
}

 /*
  we always add the latest added value to the start of the list and shift the rest
  this way our data source order and our list order is the same.
  so we can easily identify items through the Listview position and array index

  if you would only want to reverse the view and not the datasource you could do this

    @Override
    public Search getItem(int position) {
        //int reverseIndex = (history.size()-1) - position;
        //return history.get(reverseIndex);
    }

 */
