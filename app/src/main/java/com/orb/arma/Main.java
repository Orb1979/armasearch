package com.orb.arma;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.orb.arma.adapters.PagerAdapter;
import com.orb.arma.beans.Search;
import com.orb.arma.beans.Server;
import com.orb.arma.fragments.FragSearch;
import com.orb.arma.fragments.FragServerList;
import com.orb.arma.fragments.FragServerDetail;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Main extends ActionBarActivity implements FragSearch.OnCallback, FragServerList.OnCallback {
    private PagerAdapter pagerAdapter;
    public final static String FRAG_DATA_KEY = "FRAG_DATA_KEY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        //the pager adapter that handles the viewPager, Actionbar, Tab Listeners
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), viewPager, actionBar);

        //add the default fragment
        FragSearch frag = new FragSearch();
        pagerAdapter.addTab(frag, 0, getString(R.string.tabTitleSearch));

	}

    //-- Callbacks from fragments

    @Override
    public void onSearchQuery(Search search) {
        int position = 1;
        FragServerList fragment = (FragServerList) pagerAdapter.getItem(position);

        //creating fragment
        if(fragment == null){
            Bundle bundle = new Bundle();
            bundle.putSerializable(FRAG_DATA_KEY, search);
            fragment = new FragServerList();
            fragment.setArguments(bundle);
            pagerAdapter.addTab(fragment, position, getString(R.string.tabTitleServerList));
        }
        //re-using fragment with updated data
        else{
            fragment.reOpened(search);
        }
        //switch to the fragment
        pagerAdapter.switchToTab(position);

    }

    @Override
    public void onServerSelected(Server server) {
        int position = 2;
        FragServerDetail fragment = (FragServerDetail) pagerAdapter.getItem(position);

        //creating fragment
        if(fragment == null){
            Bundle bundle = new Bundle();
            bundle.putSerializable(FRAG_DATA_KEY, server);
            fragment = new FragServerDetail();
            fragment.setArguments(bundle);
            pagerAdapter.addTab(fragment, position, getString(R.string.tabTileServerDetail));
        }
        //re-using fragment with updated data
        else{
            fragment.reOpened(server);
        }
        //switch to the fragment
        pagerAdapter.switchToTab(position);

    }


    @Override
    public void onBackPressed() {
        if(pagerAdapter.getCurrentPageId() == 0){
            //on first page - ask to close app
            new AlertDialog.Builder(this)
                .setMessage(getString(R.string.dialogExitAppMessage))
                .setPositiveButton(getString(R.string.dialogExitAppPositive), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.dialogExitAppNegative), null)
                .show();
        }else{
            //else switch back a page in navigation hierarchy
            pagerAdapter.switchToTab(pagerAdapter.getCurrentPageId()-1);

            //else switch to last opened page
            //pagerAdapter.switchToTab(pagerAdapter.getPrevPageId());
        }
    }
}






