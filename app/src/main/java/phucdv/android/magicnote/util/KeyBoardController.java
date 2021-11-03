package phucdv.android.magicnote.util;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import phucdv.android.magicnote.noteinterface.ShareComponents;

public class KeyBoardController {

    public interface OnKeyBoardChange{
        public void onShowKeyBorad();
        public void onHideKeyBorad();
    }

    public static boolean hideKeyboard(Activity activity, OnKeyBoardChange onKeyBoardChange) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if(onKeyBoardChange != null){
            onKeyBoardChange.onHideKeyBorad();
        }
        return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean showKeyboard(Activity activity, OnKeyBoardChange onKeyBoardChange) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if(onKeyBoardChange != null){
            onKeyBoardChange.onShowKeyBorad();
        }
        return imm.showSoftInput(view, 0);
    }

    public static boolean hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        return imm.showSoftInput(view, 0);
    }
}
