package com.orb.arma.CustomViews;

/**
Some usability improvements to a EditText

When pressing 'done' or 'next' in keyboard:
    - clear the focus
    - hide the keyboard

When pressing back button
    - clear the focus of the textField
    - (keyboard will be closed by default)
*/

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.orb.arma.utils.Utils;

public class CustomEditText extends EditText {

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        //always use done, and not next
        this.setImeOptions(EditorInfo.IME_ACTION_DONE);

        this.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch(result) {
                    case EditorInfo.IME_ACTION_DONE:
                        Utils.hideKeyBoardAndFocus(getContext(), CustomEditText.this);
                        clearFocus();
                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        Utils.hideKeyBoardAndFocus(getContext(), CustomEditText.this);
                        clearFocus();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            clearFocus();
        }

        return super.onKeyPreIme(keyCode, event);
    }
}

