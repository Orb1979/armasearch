package com.orb.arma.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 extends a button, with a state (on/off)

 There is also a ToggleButon widget, which can do the same
 But then we also need to style it
 */
public class ToggleButton extends Button {
    private boolean stateOn = true;

    //default constructors:

    public ToggleButton(Context context) {
        super(context);
    }

    public ToggleButton (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleButton (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    public void setStateOn(Boolean value, String text){
        stateOn = value;
        this.setText(text);
    }

    public boolean getStateOn(){
        return stateOn;
    }


}
