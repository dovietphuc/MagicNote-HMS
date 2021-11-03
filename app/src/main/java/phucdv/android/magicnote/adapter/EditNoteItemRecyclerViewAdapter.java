package phucdv.android.magicnote.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.phucdvb.drawer.DrawerActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.data.BaseItem;
import phucdv.android.magicnote.data.checkboxitem.CheckboxItem;
import phucdv.android.magicnote.data.imageitem.ImageItem;
import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.data.textitem.TextItem;
import phucdv.android.magicnote.noteinterface.OnKeyClick;
import phucdv.android.magicnote.ui.editnote.EditNoteFragment;
import phucdv.android.magicnote.util.Constants;
import phucdv.android.magicnote.util.KeyBoardController;
import phucdv.android.magicnote.util.UndoRedoEditText;
import uk.co.senab.photoview.PhotoViewAttacher;

public class EditNoteItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_BLANK = -1;
    private final int TYPE_TEXT = 0;
    private final int TYPE_CHECKBOX = 1;
    private final int TYPE_IMAGE = 2;


    public static final int STATE_ADD = 0;
    public static final int STATE_MODIFY = 1;
    public static final int STATE_DELETE = 2;
    public static final int STATE_NONE = 3;

    private final HashMap<BaseItem, Integer> mHashItem;
    private final HashMap<Integer, Integer> mTextCount = new HashMap<>();
    private final HashMap<Integer, Integer> mCheckboxtextCount = new HashMap<>();
    private int mNoneTextItemCount = 0;
    private final SortedList<BaseItem> mItemSortedList = new SortedList<BaseItem>(BaseItem.class, new SortedList.Callback<BaseItem>() {
        @Override
        public int compare(BaseItem o1, BaseItem o2) {
            return (int) (o1.getOrder_in_parent() - o2.getOrder_in_parent());
        }

        @Override
        public void onChanged(int position, int count) {

        }

        @Override
        public boolean areContentsTheSame(BaseItem oldItem, BaseItem newItem) {
            return false;
        }

        @Override
        public boolean areItemsTheSame(BaseItem item1, BaseItem item2) {
            if(item1 instanceof TextItem && item2 instanceof TextItem){
                return item1.getId() == item2.getId();
            }
            if(item1 instanceof CheckboxItem && item2 instanceof CheckboxItem){
                return item1.getId() == item2.getId();
            }
            if(item1 instanceof ImageItem && item2 instanceof ImageItem){
                return item1.getId() == item2.getId();
            }
            return false;
        }

        @Override
        public void onInserted(int position, int count) {
        }

        @Override
        public void onRemoved(int position, int count) {
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
        }
    });

    private boolean mIsItemNewAdd = false;
    private Context mContext;
    private int mFocusPosition = -1;
    private final Stack<HistoryItem> mPrevious = new Stack<>();
    private final Stack<HistoryItem> mFollowing = new Stack<>();

    public void clearData(){
        mItemSortedList.clear();
        mHashItem.clear();
        mFocusPosition = -1;
        mIsItemNewAdd = false;
        mPrevious.clear();
        mFollowing.clear();
        mCheckboxtextCount.clear();
        mTextCount.clear();
        mNoneTextItemCount = 0;
    }

    public EditNoteItemRecyclerViewAdapter(Context context) {
        mHashItem = new HashMap<>();
        mContext = context;
    }

    public <T> void addValues(List<BaseItem> items, Class<T> tClass) {
        if(tClass.equals(TextItem.class)){
            for(int i = 0; i < mItemSortedList.size(); i++){
                BaseItem item = mItemSortedList.get(i);
                if(item instanceof TextItem){
                    mItemSortedList.removeItemAt(i);
                    mHashItem.remove(item);
                }
            }
        } else if(tClass.equals(CheckboxItem.class)){
            for(int i = 0; i < mItemSortedList.size(); i++){
                BaseItem item = mItemSortedList.get(i);
                if(item instanceof CheckboxItem){
                    mItemSortedList.removeItemAt(i);
                    mHashItem.remove(item);
                }
            }
        } else if(tClass.equals(ImageItem.class)){
            for(int i = 0; i < mItemSortedList.size(); i++){
                BaseItem item = mItemSortedList.get(i);
                if(item instanceof ImageItem){
                    mItemSortedList.removeItemAt(i);
                    mHashItem.remove(item);
                }
            }
        }
        for (BaseItem item : items) {
            mHashItem.put(item, STATE_NONE);
        }
        mItemSortedList.addAll(items);
        notifyDataSetChanged();
    }

    public void addCheckItem(String content, boolean isChecked){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        addItem(new CheckboxItem(Constants.UNKNOW_PARENT_ID, 0, isChecked, content,
                firebaseUser != null ? firebaseUser.getUid() : null, true));
    }

    public void addCheckItem(String content, boolean isChecked, int position){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        addItem(new CheckboxItem(Constants.UNKNOW_PARENT_ID, 0, isChecked, content,
                firebaseUser != null ? firebaseUser.getUid() : null, true), position);
    }

    public void addTextItem(String content){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        addItem(new TextItem(Constants.UNKNOW_PARENT_ID, 0, content,
                firebaseUser != null ? firebaseUser.getUid() : null, true));
    }

    public void addTextItem(String content, int position){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        addItem(new TextItem(Constants.UNKNOW_PARENT_ID, 0, content,
                firebaseUser != null ? firebaseUser.getUid() : null, true), position);
    }

    public void addImageItem(String path){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        addItem(new ImageItem(Constants.UNKNOW_PARENT_ID, 0, path,
                firebaseUser != null ? firebaseUser.getUid() : null, true));
    }

    public void addImageItem(String path, int position){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        addItem(new ImageItem(Constants.UNKNOW_PARENT_ID, 0, path,
                firebaseUser != null ? firebaseUser.getUid() : null, true), position);
    }

    public void addItem(BaseItem item){
        if(mItemSortedList.size() > 0 && mItemSortedList.get(mItemSortedList.size() - 1) instanceof TextItem
                && ((TextItem) mItemSortedList.get(mItemSortedList.size() - 1)).getContent().isEmpty()){
            removeItem(mItemSortedList.size() - 1);
        }
        addItem(item, mItemSortedList.size());
    }

    public void addItem(BaseItem item, int position){
        BaseItem baseItem1 = null;
        BaseItem baseItem2 = null;
        if(position < mItemSortedList.size() && position >= 1){
            baseItem1 = mItemSortedList.get(position - 1);
            baseItem2 = mItemSortedList.get(position);
        }

        if(baseItem1 == null || baseItem2 == null){
            item.setOrder_in_parent(position * 1000L);
            mItemSortedList.add(item);
        } else {
            item.setOrder_in_parent(baseItem1.getOrder_in_parent() + (baseItem2.getOrder_in_parent()
                    - baseItem1.getOrder_in_parent())/2);
            mItemSortedList.add(item);
        }
        mHashItem.put(item, STATE_ADD);
        mIsItemNewAdd = true;
        notifyItemInserted(position);
        mPrevious.push(new HistoryItem(item, STATE_ADD, position));
        if(item instanceof ImageItem){
            mNoneTextItemCount++;
        }
        mFocusPosition = position;
    }

    private void addItemWithoutStack(BaseItem item, int position){
        BaseItem baseItem1 = null;
        BaseItem baseItem2 = null;
        if(position < mItemSortedList.size() && position > 1){
            baseItem1 = mItemSortedList.get(position - 1);
            baseItem2 = mItemSortedList.get(position);
        }

        if(baseItem1 == null || baseItem2 == null){
            item.setOrder_in_parent(mItemSortedList.size() * 1000L);
            mItemSortedList.add(item);
            position = mItemSortedList.size() - 1;
        } else {
            item.setOrder_in_parent(baseItem1.getOrder_in_parent() + (baseItem2.getOrder_in_parent()
                    - baseItem1.getOrder_in_parent())/2);
            mItemSortedList.add(item);
        }
        mHashItem.put(item, STATE_ADD);
        mIsItemNewAdd = true;
        notifyItemInserted(position);
        if(item instanceof ImageItem){
            mNoneTextItemCount++;
        }
    }

    public void removeItem(int position){
        BaseItem baseItem = mItemSortedList.get(position);
        if(baseItem instanceof ImageItem){
            mNoneTextItemCount--;
        }
        mHashItem.put(baseItem, STATE_DELETE);
        mItemSortedList.removeItemAt(position);
        if(!(baseItem instanceof TextItem) && mItemSortedList.size() == 0){
            addTextItem("");
        }
        notifyItemRemoved(position);
        mPrevious.push(new HistoryItem(baseItem, STATE_DELETE, position));
    }

    private void removeWithoutStack(int position){
        BaseItem baseItem = mItemSortedList.get(position);
        if(baseItem instanceof ImageItem){
            mNoneTextItemCount--;
        }
        mHashItem.put(baseItem, STATE_DELETE);
        mItemSortedList.removeItemAt(position);
        notifyItemRemoved(position);
    }

    public void focusOnPosition(int position){
        mFocusPosition = position;
        notifyItemChanged(position);
    }

    public void undo(){
        if(!mPrevious.isEmpty()) {
            HistoryItem historyItem = mPrevious.peek();
            if (historyItem.state == STATE_ADD) {
                removeWithoutStack(historyItem.position);
                historyItem.state = STATE_DELETE;
            } else if(historyItem.state == STATE_DELETE) {
                addItemWithoutStack(historyItem.baseItem, historyItem.position);
                historyItem.state = STATE_ADD;
            } else if (historyItem.state == STATE_MODIFY) {
            }
            mFollowing.push(historyItem);
            mPrevious.pop();
        }
    }

    public void redo(){
        if(!mFollowing.isEmpty()) {
            HistoryItem historyItem = mFollowing.peek();
            if (historyItem.state == STATE_DELETE) {
                addItemWithoutStack(historyItem.baseItem, historyItem.position);
                historyItem.state = STATE_ADD;
            } else if(historyItem.state == STATE_ADD) {
                removeWithoutStack(historyItem.position);
                historyItem.state = STATE_DELETE;
            } else if (historyItem.state == STATE_MODIFY) {
            }
            mPrevious.push(historyItem);
            mFollowing.pop();
        }
    }

    public HashMap<BaseItem, Integer> getHashMap(){
        return mHashItem;
    }

    public SortedList<BaseItem> getAdapterList(){
        return mItemSortedList;
    }

    public long getAllTextCount(){
        int count = 0;
        for(Integer i : mTextCount.keySet()){
            count += mTextCount.get(i);
        }
        for(Integer i : mCheckboxtextCount.keySet()){
            count += mCheckboxtextCount.get(i);
        }
        return count;
    }

    public String getAllTextContent(){
        String textContent = "";
        for(int i = 0; i < mItemSortedList.size(); i++){
            BaseItem item = mItemSortedList.get(i);
            if(item instanceof TextItem){
                textContent += ((TextItem)item).getContent() + "\n";
            } else if(item instanceof CheckboxItem){
                textContent += ((CheckboxItem)item).getContent() + "\n";
            }
        }
        return textContent;
    }

    public int getFocusPosition(){
        return mFocusPosition;
    }

    public Context getContext(){
        return mContext;
    }

    public int getNoneTextItemCount(){
        return mNoneTextItemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == mItemSortedList.size()) return TYPE_BLANK;

        BaseItem item = mItemSortedList.get(position);
        if (item instanceof TextItem) {
            return TYPE_TEXT;
        } else if (item instanceof CheckboxItem) {
            return TYPE_CHECKBOX;
        } else if (item instanceof ImageItem){
            return TYPE_IMAGE;
        }
        return super.getItemViewType(position);
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_CHECKBOX:
                return new CheckboxItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.checkbox_item, parent, false));
            case TYPE_TEXT:
                return new TextItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.text_item, parent, false));
            case TYPE_IMAGE:
                return new ImageItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.image_item, parent, false));
            default:
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fake_item_margin, parent, false)){};
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_CHECKBOX:
                ((CheckboxItemViewHolder) holder)
                        .bind(((CheckboxItem) mItemSortedList.get(position)).getContent(),
                                ((CheckboxItem) mItemSortedList.get(position)).isIs_checked());
                break;
            case TYPE_TEXT:
                ((TextItemViewHolder) holder)
                        .bind(((TextItem) mItemSortedList.get(position)).getContent());
                break;
            case TYPE_IMAGE:
                ((ImageItemViewHolder)holder)
                        .bind(((ImageItem)mItemSortedList.get(position)).getPath());
        }
    }

    @Override
    public int getItemCount() {
        return mItemSortedList.size() + 1;
    }

    public class TextItemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final UndoRedoEditText mEditTextView;

        public TextItemViewHolder(View view) {
            super(view);
            mView = view;
            mEditTextView = view.findViewById(R.id.textInputText);
            mEditTextView.addTextChangedListener(new TextWatcher() {
                private String lastText = "";
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mTextCount.put(getLayoutPosition(), s.toString().trim().length());
                    BaseItem item = mItemSortedList.get(getLayoutPosition());
                    ((TextItem)item).setContent(s.toString());
                    Integer hashState = mHashItem.get(item);
                    if(hashState != null && hashState != STATE_ADD) {
                        mHashItem.put(item, STATE_MODIFY);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    lastText = s.toString();
                }
            });
            mEditTextView.setOnKeyClick(event -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    if (mEditTextView.getText().toString().length() == 0 && getLayoutPosition() != 0){
                        removeItem(getLayoutPosition());
                        return true;
                    }
                }
                return false;
            });
            mEditTextView.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus){
                    mFocusPosition = getLayoutPosition();
                }
            });
        }

        public void bind(String text) {
            mEditTextView.setText(text);
            if(mIsItemNewAdd || mFocusPosition == getLayoutPosition()){
                mEditTextView.requestFocus(mEditTextView.getText().length());
                KeyBoardController.showKeyboard((Activity) mContext);
                mIsItemNewAdd = false;
            }
        }
    }

    public class CheckboxItemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CheckBox mCheckBoxView;
        public final UndoRedoEditText mEditTextView;

        public CheckboxItemViewHolder(View view) {
            super(view);
            mView = view;
            mEditTextView = view.findViewById(R.id.textInputText);
            mEditTextView.addTextChangedListener(new TextWatcher() {
                private String lastText = "";
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length() > 0 && s.charAt(s.length() - 1) == '\n') {
                        addCheckItem("", false, getLayoutPosition() + 1);
                        return;
                    }
                    mCheckboxtextCount.put(getLayoutPosition(), s.toString().trim().length());
                    BaseItem item = mItemSortedList.get(getLayoutPosition());
                    ((CheckboxItem)item).setContent(s.toString());
                    Integer hashState = mHashItem.get(item);
                    if(hashState != null && hashState != STATE_ADD) {
                        mHashItem.put(item, STATE_MODIFY);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.length() > 0 && s.charAt(s.length() - 1) == '\n') {
                        s.replace(0, s.length(), lastText);
                    } else {
                        lastText = s.toString();
                    }
                }
            });

            mEditTextView.setOnKeyClick(event -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    if (mEditTextView.getText().toString().length() == 0){
                        removeItem(getLayoutPosition());
                        return true;
                    }
                }
                return false;
            });

            mEditTextView.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus){
                    mFocusPosition = getLayoutPosition();
                }
            });

            mCheckBoxView = view.findViewById(R.id.checkBox);
            mCheckBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                private boolean lastChecked = false;
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    BaseItem item = mItemSortedList.get(getLayoutPosition());
                    ((CheckboxItem)item).setIs_checked(isChecked);
                    Integer hashState = mHashItem.get(item);
                    if(hashState != null && hashState != STATE_ADD) {
                        mHashItem.put(item, STATE_MODIFY);
                    }
                    lastChecked = isChecked;
                }
            });
        }

        public void bind(String text, boolean isChecked) {
            mCheckBoxView.setChecked(isChecked);
            mEditTextView.setText(text);
            if(mIsItemNewAdd || mFocusPosition == getLayoutPosition()){
                mEditTextView.requestFocus(mEditTextView.getText().length());
                KeyBoardController.showKeyboard((Activity) mContext);
                mIsItemNewAdd = false;
            }
        }
    }

    public class ImageItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public View mView;

        public ImageItemViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.imageView);
            PhotoViewAttacher pAttacher;
            pAttacher = new PhotoViewAttacher(mImageView);
            pAttacher.update();
            pAttacher.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(mContext)
                            .setTitle(R.string.warning)
                            .setMessage("Delete this image")
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeItem(getLayoutPosition());
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return true;
                }
            });
        }

        public void bind(String path){
            setPathForImageView(mImageView, path);
        }

        public void setPathForImageView(ImageView imageView, String path){
            Glide.with(mContext)
                        .load(path)
                        .into(imageView);
        }
    }

    private static class HistoryItem{
        public BaseItem baseItem;
        public int state;
        public int position;

        public HistoryItem(BaseItem baseItem, int state, int position) {
            this.baseItem = baseItem;
            this.state = state;
            this.position = position;
        }
    }
}