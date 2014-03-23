package com.orb.arma.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.orb.arma.R;
import com.orb.arma.adapters.FriendAdapter;
import com.orb.arma.adapters.HistoryAdapter;
import com.orb.arma.beans.Search;
import com.orb.arma.utils.Utils;
import java.util.ArrayList;

public class FragSearch extends Fragment {
    private OnCallback mCallback;
    private FriendAdapter friendAdapter;
    private HistoryAdapter historyAdapter;

    // Container Activity must implement this interface
    public interface OnCallback {
        public void onSearchQuery(Search search);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCallback ");
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        //Setup search bean
        Search.setUrlInfo(getActivity().getString(R.string.xmlServerListPrefix),
                          getActivity().getString(R.string.xmlServerListPostfix));

        if(rootView != null){
            //------------ views ------------
            final EditText inputSearch = (EditText) rootView.findViewById(R.id.inputFilterName);
            final EditText inputFilterCountry = (EditText) rootView.findViewById(R.id.inputFilterCountry);
            final EditText inputFilterPlayerCount = (EditText) rootView.findViewById(R.id.inputFilterPlayerCount);
            final EditText inputAddFriend = (EditText) rootView.findViewById(R.id.inputAddFriend);

            final Button btnSearch = (Button) rootView.findViewById(R.id.btnSearch);
            final Button btnAddFriend = (Button) rootView.findViewById(R.id.btnAddFriend);
            final Button btnExpandSearch = (Button) rootView.findViewById(R.id.btnExpandSearch);
            final Button btnExpandFilters = (Button) rootView.findViewById(R.id.btnExpandFilters);
            final Button btnExpandFriends = (Button) rootView.findViewById(R.id.btnExpandFriends);
            final ImageButton btnInfoFriends = (ImageButton) rootView.findViewById(R.id.btnInfoFriends);
            final CheckBox cbFilterDedicated = (CheckBox) rootView.findViewById(R.id.cbFilterDedicated);
            final CheckBox cbAddWildcards = (CheckBox) rootView.findViewById(R.id.cbAddWildcards);

            final ListView lvFriends = (ListView) rootView.findViewById(R.id.lvFriends);
            final ListView lvHistory = (ListView) rootView.findViewById(R.id.lvHistory);

            final View viewExpandSearch = rootView.findViewById(R.id.viewExpandSearch);
            final View viewExpandFilters = rootView.findViewById(R.id.viewExpandFilters);
            final View viewExpandFriends = rootView.findViewById(R.id.viewExpandFriends);

            //------------ adapters ------------

            //get layout inflater for custom views
            LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //init the data source for historyAdapter
            ArrayList<Search> history = new ArrayList<Search>();

            //friend list
            friendAdapter = new FriendAdapter(getActivity());
            final LinearLayout friendFooter = (LinearLayout)li.inflate( R.layout.friends_footer, null, false );
            lvFriends.addFooterView(friendFooter, null, false);
            lvFriends.setAdapter(friendAdapter);
            lvFriends.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

            //history list
            historyAdapter = new HistoryAdapter(getActivity(), history );
            final LinearLayout historyFooter = (LinearLayout)li.inflate( R.layout.history_footer, null, false );
            final LinearLayout historyHeader = (LinearLayout)li.inflate( R.layout.history_header, null, false );
            lvHistory.addFooterView(historyFooter, null, false);
            lvHistory.addHeaderView(historyHeader, null, false);
            lvHistory.setAdapter(historyAdapter);

            //check if we want to add the footer (for clearing all data)
            showHideFooter(friendFooter, FriendAdapter.friends, 2);
            showHideFooter(historyFooter, historyAdapter.getHistory(), 2);

            //------------ handlers ------------

            //footer friends
            if (friendFooter != null) {
                friendFooter.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                            .setTitle(getActivity().getString(R.string.dialogTitleDeleteFriends))
                            .setMessage(getActivity().getString(R.string.dialogMessageDeleteFriends))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    cleanFriends(getActivity().getApplicationContext());
                                    showHideFooter(friendFooter, FriendAdapter.friends, 2);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {/*do nothing*/}
                            }).show();
                    }
                });
            }

            //footer history
            if (historyFooter != null) {
                historyFooter.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                            .setTitle(getActivity().getString(R.string.dialogTitleClearHistory))
                            .setMessage(getActivity().getString(R.string.dialogMessageClearHistory))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    cleanHistory(getActivity().getApplicationContext());
                                    showHideFooter(historyFooter, historyAdapter.getHistory(), 2);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {/*do nothing*/}
                            }).show();
                    }
                });
            }

            //history item click
            lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   Utils.hideKeyBoardAndFocus(getActivity(), inputSearch);

                   //Search for the servers based on the history input
                   Search historySearch = historyAdapter.getItem((int)id);
                   searchForServers(historySearch, (int)id);
                }
            });

            //click handler (for all buttons)
            View.OnClickListener onClickListener = new View.OnClickListener(){
                public void onClick(View v) {
                    //hide input on click of parent view
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    switch (v.getId()){
                        case R.id.btnSearch        : handleSearchClick(); break;
                        case R.id.btnAddFriend     : addFriend(); break;
                        case R.id.btnExpandSearch  : toggleExpandView(viewExpandSearch, btnExpandSearch); break;
                        case R.id.btnExpandFilters : toggleExpandView(viewExpandFilters, btnExpandFilters); break;
                        case R.id.btnExpandFriends : toggleExpandView(viewExpandFriends, btnExpandFriends); break;
                        case R.id.btnInfoFriends :
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.popup_wildcard_info);
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();
                        break;
                    }
                }

                private void handleSearchClick(){
                    Utils.hideKeyBoardAndFocus(getActivity(), inputSearch);
                    String searchName = Utils.getStringFromView(inputSearch, "", 2);

                    if(searchName.equals("")){
                        inputSearch.setHint(getActivity().getString(R.string.searchValidateMinChars));
                     }else{
                        //clear input
                        inputSearch.setText("");

                        //get values from views
                        int playerCountFilter   = Utils.getIntegerFromView(inputFilterPlayerCount, 0, 1);
                        String countryFilter    = Utils.getStringFromView(inputFilterCountry, "", 2);
                        Boolean dedicatedFilter = cbFilterDedicated.isChecked();

                        //do the search
                        Search currentSearch = new Search(searchName, playerCountFilter, countryFilter, dedicatedFilter);
                        searchForServers(currentSearch, -1);

                        //check if we want to add the footer item (for clearing all data)
                        showHideFooter(historyFooter, historyAdapter.getHistory(), 2);
                    }
                }

                private void addFriend(){
                    Utils.hideKeyBoardAndFocus(getActivity(), inputAddFriend);
                    String friendName = Utils.getStringFromView(inputAddFriend, "", 2);

                    if(friendName != null){
                        if(friendName.equals("")){
                            inputAddFriend.setHint(getActivity().getString(R.string.addFriendValidateMinChars));
                        }else{
                            //clear input
                            inputAddFriend.setText("");

                            //add wildcard if the checkbox 'surround with wildcards' is checked
                            if(cbAddWildcards.isChecked()){
                                friendName = "*"+friendName+"*";
                            }

                            //save the data to the datasource
                            friendAdapter.save(friendName);

                            //check if we want to add the footer item (for clearing all data)
                            showHideFooter(friendFooter, FriendAdapter.friends, 2);
                        }
                    }
                }
            };

            // Focus listener TextView
            // collapseView views on focus, if they are open
            inputAddFriend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        if(viewExpandSearch.getVisibility() == View.VISIBLE){
                            toggleExpandView(viewExpandSearch, btnExpandSearch);
                        }if(viewExpandFilters.getVisibility() == View.VISIBLE){
                            toggleExpandView(viewExpandFilters, btnExpandFilters);
                        }
                    }
                }
            });

            //set the listeners
            btnSearch.setOnClickListener(onClickListener);
            btnAddFriend.setOnClickListener(onClickListener);
            btnInfoFriends.setOnClickListener(onClickListener);
            btnExpandSearch.setOnClickListener(onClickListener);
            btnExpandFilters.setOnClickListener(onClickListener);
            btnExpandFriends.setOnClickListener(onClickListener);

            //to remove focus from EditText if you click outside
            rootView.setOnClickListener(onClickListener);
        }

        return rootView;
    }

    private void searchForServers(Search search, int historyIndex){
        historyAdapter.save(search, historyIndex);
        mCallback.onSearchQuery(search);
    }

    private void showHideFooter(final View footer, ArrayList datasource, int minRecords){
        //check if we should show the footer
        if(datasource.size() >= minRecords){
            footer.setVisibility(View.VISIBLE);
        }else{
            footer.setVisibility(View.GONE);
        }
    }

    private void toggleExpandView(View v, Button btn){
        if(v.getVisibility() == View.VISIBLE ){
            btn.setText("+");
            v.setVisibility(View.GONE);
        }else{
            btn.setText("-");
            v.setVisibility(View.VISIBLE);
        }
    }

    public void cleanHistory(Context context){
        cleanSharedPref(context, context.getString(R.string.spNameHistory));
        historyAdapter.loadHistory(context);
        historyAdapter.updateHistory();
    }
    public void cleanFriends(Context context){
        cleanSharedPref(context, context.getString(R.string.spNameFriends));
        friendAdapter.loadFriends(context);
        friendAdapter.updateFriends();

    }

    public void cleanSharedPref(Context context, String spName){
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.commit();
    }
}