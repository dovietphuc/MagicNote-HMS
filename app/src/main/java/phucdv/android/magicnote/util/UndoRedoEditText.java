package phucdv.android.magicnote.util;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Stack;
import java.util.regex.Pattern;

import phucdv.android.magicnote.noteinterface.OnKeyClick;

public class UndoRedoEditText extends androidx.appcompat.widget.AppCompatEditText {

    private OnKeyClick mOnKeyClick;

    private final Stack<String> mPrevious = new Stack<>();
    private final Stack<String> mFollowing = new Stack<>();

    public UndoRedoEditText(Context context) {
        super(context);
    }

    public UndoRedoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UndoRedoEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPrevious.push(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Linkify.addLinks(s, Linkify.ALL);
                Pattern tagMatcher = Pattern.compile(("#([ء-يA-Za-z0-9_-]+)"));
                Linkify.addLinks(s, tagMatcher, null);
            }
        });
        setLinksClickable(true);
        setAutoLinkMask(Linkify.ALL);
        Linkify.addLinks(this, Linkify.ALL);
        Pattern tagMatcher = Pattern.compile(("#([ء-يA-Za-z0-9_-]+)"));
        Linkify.addLinks(this, tagMatcher, null);
    }

    public String undo(){
        if(!mPrevious.isEmpty())
            setText(mFollowing.push(mPrevious.pop()));
        return getText() + "";
    }

    public String redo(){
        if(!mFollowing.isEmpty())
            setText(mPrevious.push(mFollowing.pop()));
        return getText() + "";
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new MyInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    public void setOnKeyClick(OnKeyClick onKeyClick){
        mOnKeyClick = onKeyClick;
    }

    private class MyInputConnection extends InputConnectionWrapper {

        public MyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (mOnKeyClick != null){
                mOnKeyClick.onKeyClick(event);
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (beforeLength == 1 && afterLength == 0) {
                // backspace
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }
}
