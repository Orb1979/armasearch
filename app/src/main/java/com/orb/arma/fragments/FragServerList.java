package com.orb.arma.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import com.orb.arma.Main;
import com.orb.arma.beans.Search;
import com.orb.arma.beans.Server;
import com.orb.arma.xml.ParseServerList;
import com.orb.arma.xml.ParseCheckForFriends;
import com.orb.arma.adapters.ServerListAdapter;
import com.orb.arma.R;
import com.orb.arma.xml.XmlUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class FragServerList extends Fragment{
    private Spinner spinnerSort;
    private ImageButton btnSortDirection;
    private Search search;
    private ListView listView;
    private OnCallback mCallback;
    private boolean isDesc = true;

    // Container Activity must implement this interface
    public interface OnCallback {
        public void onServerSelected(Server selectedServer);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()  + " must implement OnCallback ");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            this.search = (Search) getArguments().getSerializable(Main.FRAG_DATA_KEY);
        }
    }

    // on re-opening a fragment with updated data
    public void reOpened(Search search) {
        this.search = search;
        getServerData(false);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_server_list, container, false);

        if(rootView != null){

            //refresh button
            ImageButton btnRefresh = (ImageButton) rootView.findViewById(R.id.btnRefresh);
            btnRefresh.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //create the Async Class and execute its task
                    getServerData(false);
                }
            });

            //Sort Asc / Desc button (we set the listener when we have the results)
            btnSortDirection = (ImageButton) rootView.findViewById(R.id.btnSortDirection);

            //Sort spinner
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                    getActivity(), R.array.spinnerItems, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            spinnerSort = (Spinner) rootView.findViewById(R.id.spinnerSort);
            spinnerSort.setAdapter(spinnerAdapter);

            //server ListView
            listView = (ListView) rootView.findViewById(R.id.lvServerList);

            //get the severdata and populate the list
            getServerData(false);
        }

        return rootView;
	}

    public void getServerData(boolean highlightFriends){
        //clear list every time
        listView.setAdapter(null);

        //do async request for servers
        GetXMLTask task = new GetXMLTask(highlightFriends);
        task.execute(this.search);
    }

    private void executeSort(int position, List<Server> servers, ServerListAdapter adapter){
        switch (position){
            case 0: if(isDesc) Collections.sort(servers, ServerListAdapter.sortPlayersDesc);
            else       Collections.sort(servers, ServerListAdapter.sortPlayersAsc);
                break;
            case 1: if(isDesc) Collections.sort(servers, ServerListAdapter.sortNameDesc);
            else       Collections.sort(servers, ServerListAdapter.sortNameAsc);
                break;
            case 2: if(isDesc) Collections.sort(servers, ServerListAdapter.sortCountryDesc);
            else       Collections.sort(servers, ServerListAdapter.sortCountryAsc);
                break;
            case 3: if(isDesc) Collections.sort(servers, ServerListAdapter.sortStateDesc);
            else       Collections.sort(servers, ServerListAdapter.sortStateAsc);
                break;
            case 4: getServerData(true); //checking for friends
                break;
        }
        adapter.notifyDataSetChanged();
    }


    //private inner class extending AsyncTask
    private class GetXMLTask extends AsyncTask<Search, Integer, List<Server>>
    {
        boolean highlightFriends;

        public GetXMLTask(boolean highlightFriends){
            this.highlightFriends = highlightFriends;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        @Override
        protected List<Server> doInBackground(Search... search) {
            //create the parser
            ParseServerList parser = new ParseServerList();

            //get xml HttpURLConnection
            String xml = XmlUtils.getXmlFromUrl(search[0].getSearchURL());

            //parse the stream with the xml bytes
            InputStream stream = new ByteArrayInputStream(xml.getBytes());

            return parser.parse(stream, search[0]);
        }

        @Override
        protected void onPostExecute(final List<Server> servers) {
            //use the default sorting:
            spinnerSort.setSelection(0);
            Collections.sort(servers, ServerListAdapter.sortPlayersDesc);

            //set the server list adapter
            final ServerListAdapter adapter = new ServerListAdapter(getActivity(), servers);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Server selectedServer = adapter.getServers().get(position);
                    adapter.setSelectedServer(servers.get(position));

                    //servers.get(position).isSelected = true;
                    adapter.notifyDataSetChanged();

                    //Do callback to activity
                    mCallback.onServerSelected(servers.get(position));
                }
            });
            listView.setAdapter(adapter);

            //check for friends, we need do to check a xml file per server --
            if(highlightFriends){
                for(Server aServer : servers){
                    //we can skip the servers that have 0 players
                    if(aServer.getPlayers() > 0){
                        SearchForFriends taskFriends = new SearchForFriends(aServer, adapter);
                        taskFriends.execute(getActivity().getString(R.string.xmlServerPrefix) + aServer.getId());

                    }
                }
            }

            //set Sort spinner handler
            spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinnerSort.setSelection(position);
                    executeSort(position, servers, adapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //we do nothing i guess, stupid Interface
                }
            });

            //change the sort direction
            btnSortDirection.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    //toggle boolean and image
                    if(isDesc){
                        isDesc = false;
                        btnSortDirection.setImageResource(R.drawable.sort_asc);
                    }else{
                        isDesc = true;
                        btnSortDirection.setImageResource(R.drawable.sort_desc);
                    }
                    //reverse serverList
                    Collections.reverse(servers);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }


    //private inner class extending AsyncTask
    private class SearchForFriends extends AsyncTask<String, Integer, ServerListAdapter>
    {
        private Server aServer;
        private ServerListAdapter adapter;

        public SearchForFriends(Server aSserver, ServerListAdapter adapter){
            this.aServer = aSserver;
            this.adapter = adapter;
        }

        @Override
        protected ServerListAdapter doInBackground(String... urls) {
            //create the parser
            ParseCheckForFriends parser = new ParseCheckForFriends();

            //get xml HttpURLConnection
            String xml = XmlUtils.getXmlFromUrl(urls[0]);

            //parse the stream with the xml bytes
            InputStream stream = new ByteArrayInputStream(xml.getBytes());

            return parser.parse(stream, aServer, adapter);
        }

        @Override
        protected void onPostExecute(ServerListAdapter adapter) {
            //invoked on the UI thread after the background computation finishes.

            Collections.sort(adapter.getServers(), ServerListAdapter.sortFriends);
            adapter.notifyDataSetChanged();

        }
    }
}
