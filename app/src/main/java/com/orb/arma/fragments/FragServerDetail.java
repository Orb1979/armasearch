package com.orb.arma.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.orb.arma.Main;
import com.orb.arma.R;
import com.orb.arma.adapters.PlayerListAdapter;
import com.orb.arma.beans.Server;
import com.orb.arma.beans.ServerDetail;
import com.orb.arma.xml.ParseServerDetails;
import com.orb.arma.xml.XmlUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class FragServerDetail extends Fragment {
    private Server server;
    private ParseServerDetails parser;
    private View rootView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.server = (Server) getArguments().getSerializable(Main.FRAG_DATA_KEY);
    }

    // on re-opening an existing tab
    public void reOpened(Server server) {
        this.server = server;
        getServerDetailData(rootView);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_server_detail, container, false);

        //we keep a reference of the view (for reOpened())
        this.rootView = rootView;

        getServerDetailData(rootView);

		return rootView;
	}

    private void getServerDetailData(View rootView){
        final TextView tvServerName  = (TextView) rootView.findViewById(R.id.server_name);
        final ImageView ivPassword   = (ImageView) rootView.findViewById(R.id.password);
        final TextView tvCountry     = (TextView) rootView.findViewById(R.id.country);
        final TextView tvState       = (TextView) rootView.findViewById(R.id.state);
        final TextView tvAdres       = (TextView) rootView.findViewById(R.id.adres);
        final TextView tvPlayerCount = (TextView) rootView.findViewById(R.id.playercount);
        final TextView tvMaxPlayers  = (TextView) rootView.findViewById(R.id.maxplayercount);
        final TextView tvMission     = (TextView) rootView.findViewById(R.id.mission);
        final TextView tvIsland      = (TextView) rootView.findViewById(R.id.island);
        final TextView tvStartTime   = (TextView) rootView.findViewById(R.id.startTime);
        final ListView lvPlayers     = (ListView) rootView.findViewById(R.id.players);
        final View collapseView      = rootView.findViewById(R.id.collapseView);
        final Button btnExpand       = (Button) rootView.findViewById(R.id.btnExpand);

        //we already have these values from the Server Bean
        tvServerName.setText(server.getName());
        tvCountry.setText(server.getCountry());
        if(server.getPassword()){
            ivPassword.setVisibility(View.VISIBLE);
        }else{
            ivPassword.setVisibility(View.GONE);
        }

        // make the listView filterable
        // lvPlayers.setTextFilterEnabled(true);

        // inner class closure,
        AsyncTask<String, Integer, ServerDetail> active = new AsyncTask<String, Integer, ServerDetail>() {
            @Override
            protected ServerDetail doInBackground(String... urls) {
                //create the parser
                parser = new ParseServerDetails();

                //get xml HttpURLConnection
                String xml = XmlUtils.getXmlFromUrl(urls[0]);

                //parse the stream with the xml bytes
                InputStream stream = new ByteArrayInputStream(xml.getBytes());

                return parser.parse(stream, server);
            }

            protected void onProgressUpdate(Integer... progress) {
                //create a loader
                //setProgressPercent(progress[0]);
            }

            @Override
            protected void onPostExecute(ServerDetail serverDetail) {
                //invoked on the UI thread after the background computation finishes.
                tvState.setText(serverDetail.state);
                tvAdres.setText(serverDetail.host + " : " + serverDetail.port);
                tvMission.setText(serverDetail.mission);
                tvIsland.setText(serverDetail.island);
                tvStartTime.setText(serverDetail.startTime);
                tvPlayerCount.setText(serverDetail.playerCount);
                tvMaxPlayers.setText(serverDetail.maxPlayers);

                if (serverDetail.players != null) {
                    PlayerListAdapter adapter = new PlayerListAdapter(getActivity(), serverDetail.players, serverDetail.hasFriends);

                    LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout historyHeader = (LinearLayout) li.inflate(R.layout.players_header, null, false);
                    if (lvPlayers.getHeaderViewsCount() == 0)
                        lvPlayers.addHeaderView(historyHeader, null, false);

                    // no longer saving data from inactive players, so the filter is no longer needed
                    // adapter.getFilter().filter("active");
                    // adapter.notifyDataSetChanged();

                    lvPlayers.setAdapter(adapter);


                    btnExpand.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (collapseView.getVisibility() == View.VISIBLE) {
                                collapseView.setVisibility(View.GONE);
                            } else {
                                collapseView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        }
        //execute clossure
        .execute( getActivity().getString(R.string.xmlServerPrefix) + server.getId());

    }
}
