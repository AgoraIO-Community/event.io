package com.example.evan.eventio;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MethodMisc {
    public static void closeKeyboard(FragmentActivity activity){
        View view = activity.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
