package com.orb.arma.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.orb.arma.CustomViews.ToggleButton;
import com.orb.arma.R;
import com.orb.arma.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

  /*
  data source is static so its always for easy access,
  we dont want to pass the data as a bundle to the fragment
  because if we swipe back to search, add, change, delete a friend
  we want to access current data
  */

public class FriendAdapter extends ArrayAdapter<String> {
    private Activity context;
    public static ArrayList<String> friends = new ArrayList<String>();

    public FriendAdapter(Activity context) {
        super(context, R.layout.server_list_item, friends);
        this.context = context;

        SharedPrefAdapter.loadFriendList(context, friends);
    }

    /*private view holder class*/
    private static class ViewHolder {
       EditText friendName;
       Button btnRemove;
       ToggleButton btnEdit;
    }

    public long getItemId(int position) {
        return friends.indexOf(friends.get(position));
    }

    public String getItem(int position) {
        return friends.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.friends_list_item, null);
            holder = new ViewHolder();
            holder.friendName = (EditText) convertView.findViewById(R.id.friendName);
            holder.btnRemove = (Button) convertView.findViewById(R.id.btnRemove);
            holder.btnEdit = (ToggleButton) convertView.findViewById(R.id.btnSave);
            convertView.setTag(holder);
        }

        //cached view
        holder = (ViewHolder) convertView.getTag();

        //get the data from the data source
        String friendString = getItem(position);

        //Fill EditText with the value of the data source
        holder.friendName.setText(friendString);

        // and give it an ID for the edit
        holder.friendName.setId(position);
        holder.btnEdit.setId(position);

        // make a immutable copies because we cant make the holder final
        final EditText inputCopy = holder.friendName;
        final ToggleButton btnEditCopy = holder.btnEdit;

        //save or edit button
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnEditCopy.getStateOn())
                    editFriend();
                else
                    saveEditedFriend();
            }

            private void editFriend() {
                inputCopy.setEnabled(true);
                inputCopy.requestFocus();
                inputCopy.setSelection(inputCopy.getText().toString().length());
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inputCopy, InputMethodManager.SHOW_IMPLICIT);

                //toggle button to state save
                btnEditCopy.setStateOn(false, context.getString(R.string.btnEditSaveFriend));
            }

            private void saveEditedFriend() {
                int id = btnEditCopy.getId();

                //prevent out of bounds (only possible with clear sharedPreferences)
                if (id < friends.size()) {
                    String newName = Utils.getStringFromView(inputCopy, "", 2);
                    if(newName.equals("")){
                        inputCopy.setHint("2 char minimum");
                    }else{
                        //only really save it, the name was changed
                        if (!newName.equals(friends.get(id))) {
                            friends.set(id, newName);
                            SharedPrefAdapter.saveFriendList(context, friends);
                        }
                        inputCopy.setEnabled(false);
                        inputCopy.clearFocus();

                        //toggle button to edit
                        btnEditCopy.setStateOn(true, context.getString(R.string.btnEditFriend));
                    }
                }
            }

        });

        // remove a friend
        holder.btnRemove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //should also be gc, but just in case
                btnEditCopy.setOnClickListener(null);

                //remove the item from the datasource
                friends.remove(inputCopy.getId());

                //save change to sharedPreferences
                SharedPrefAdapter.saveFriendList(context, friends);

                //notify datasource has changed (so the list will update)
                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    //add the friend to datasource  and save to sharedPreferences
    public void save(String name){
        if(friends.contains(name)){
            //allready exists, set on top of list
            Collections.swap(friends, friends.indexOf(name), 0);
        }else{
            //save on top of list
            friends.add(0, name);
            SharedPrefAdapter.saveFriendList(context, friends);
        }
        notifyDataSetChanged();
    }

    // load friendlist from shared preferences
    public void loadFriends(Context context){
        SharedPrefAdapter.loadFriendList(context, friends);
    }

    public void updateFriends(){
         notifyDataSetChanged();
    }



    /*
    // Removed saving on focus out because its bugs in android 2.3 and lower
    // the second edit textfield in a listview will get a focus in and focus out on every keyboard input
    // need to many work arounds to fix this
    holder.friendName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                Log.d("test", "no focus");
                //save
            }
            else {
                Log.d("test", "focus");
            }
        }
    });
    */

}