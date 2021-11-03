package phucdv.android.magicnote.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.data.label.Label;
import phucdv.android.magicnote.data.noteandlabel.NoteWithLabels;
import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.noteinterface.OnItemLongClickListener;
import phucdv.android.magicnote.noteinterface.ShareComponents;
import phucdv.android.magicnote.ui.colorpicker.ColorPickerDialog;
import phucdv.android.magicnote.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Note}.
 * TODO: Replace the implementation with code for your data type.
 */
public class NoteItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    public final int ACTION_FILTER_CHECKBOX = 0;
    public final int ACTION_FILTER_IMAGE = 1;
    public final int ACTION_FILTER_COLOR = 2;
    public final int ACTION_FILTER_LABEL = 3;
    public final int ACTION_FILTER_TEXT = 4;
    public final int ACTION_JUST_FILTER = 5;

    public final int COLOR_NONE = ColorPickerDialog.COLOR_NONE;

    private final int TYPE_DEFAULT = 0;
    private final int TYPE_BLANK = 1;

    public final int MODE_NORMAL = 0;
    public final int MODE_SELECT = 1;
    public final int MODE_SELECT_ALL = 2;

    private int mMode = MODE_NORMAL;

    private OnItemLongClickListener mOnItemLongClickListener;

    private List<Note> mValues;
    private List<Note> mValuesFilted;
    private boolean[] mSelectedPos;
    private Note[] mNoteArr;

    private boolean mFilterCheckbox = false;
    private boolean mFilterImage = false;
    private int mFilterColor = COLOR_NONE;
    private Hashtable<String, Label> mFilterLabel = new Hashtable<>();
    private String mFilterText = "";

    private Hashtable<Long, NoteWithLabels> mNoteWithLabels = new Hashtable<>();

    public NoteItemRecyclerViewAdapter() {
        mValues = new ArrayList<>();
        mValuesFilted = mValues;
    }

    public Hashtable<Long, NoteWithLabels> getNoteWithLabels() {
        return mNoteWithLabels;
    }

    public void setNoteWithLabels(List<NoteWithLabels> noteWithLabels) {
        mNoteWithLabels.clear();
        for(NoteWithLabels noteWithLabel : noteWithLabels){
            mNoteWithLabels.put(noteWithLabel.getNote().getId(), noteWithLabel);
        }
    }

    public void setValues(List<Note> values){
        mValues = values;
        mValuesFilted = mValues;
        mNoteArr = new Note[values.size()];
        values.toArray(mNoteArr);
        mSelectedPos = new boolean[mValuesFilted.size()];
        notifyDataSetChanged();
    }

    public void setMode(int mode){
        mMode = mode;
    }

    public boolean isSelecting(){
        return mMode != MODE_NORMAL;
    }

    public void startSelect(){
        setMode(MODE_SELECT);
        Arrays.fill(mSelectedPos, false);
        notifyDataSetChanged();
    }

    public void startSelectAll(){
        setMode(MODE_SELECT_ALL);
        Arrays.fill(mSelectedPos, true);
        notifyDataSetChanged();
    }

    public void endSelect(){
        setMode(MODE_NORMAL);
        notifyDataSetChanged();
    }

    public List<Note> getSelectedList(){
        List<Note> notes = new ArrayList<>();
        for(int i = 0; i < mSelectedPos.length; i++){
            if(mSelectedPos[i]){
                notes.add(mNoteArr[i]);
            }
        }
        return notes;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public Note getItemAt(int index){
        return mValuesFilted.get(index);
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount() - 1 ? TYPE_BLANK : TYPE_DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case TYPE_BLANK:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fake_item_margin, parent, false);
                return new RecyclerView.ViewHolder(view){};
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.note_item, parent, false);
                return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            Note note = mValuesFilted.get(position);

            ((ViewHolder)holder).bind(note);
        }
    }

    @Override
    public int getItemCount() {
        return mValuesFilted.size() + 1;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                mFilterText = charSequence.toString().toLowerCase();
                NoteItemRecyclerViewAdapter.this.filter();
                FilterResults filterResults = new FilterResults();
                filterResults.values = mValuesFilted;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mValuesFilted = (List<Note>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void filter(int action, String text, int value, Label label, boolean isOn){
        switch (action){
            case ACTION_FILTER_CHECKBOX:
                mFilterCheckbox = isOn;
                break;
            case ACTION_FILTER_IMAGE:
                mFilterImage = isOn;
                break;
            case ACTION_FILTER_COLOR:
                mFilterColor = value;
                break;
            case ACTION_FILTER_LABEL:
                if(isOn) {
                    mFilterLabel.put(label.getName(), label);
                } else {
                    mFilterLabel.remove(label.getName());
                }
                break;
            case ACTION_FILTER_TEXT:
                mFilterText = text;
                break;
        }
        filter();
        notifyDataSetChanged();
    }

    private List<Note> filter(){
        List<Note> filteredList = new ArrayList<>(mValues);
        if(mFilterCheckbox){
            filteredList.removeIf(note -> !note.isHas_checkbox());
        }
        if(mFilterImage){
            filteredList.removeIf(note -> !note.isHas_image());
        }
        if(mFilterColor != COLOR_NONE){
            filteredList.removeIf(note -> note.getColor() != mFilterColor);
        }
        if(!mFilterText.isEmpty()){
            filteredList.removeIf(note -> !note.getFull_text().contains(mFilterText));
        }
        if(!mFilterLabel.isEmpty()){
            filteredList.removeIf(note -> !isNoteHasLabels(note));
        }
        mValuesFilted = filteredList;
        return mValuesFilted;
    }

    private boolean isNoteHasLabels(Note note){
        NoteWithLabels noteWithLabels = mNoteWithLabels.get(note.getId());
        if(noteWithLabels != null){
            Hashtable<String, Label> hashtable = new Hashtable<>();
            for(Label label : noteWithLabels.getLabels().getValue()){
                hashtable.put(label.getName(), label);
            }
            for(Label label : mFilterLabel.values()){
                if(!hashtable.containsKey(label.getName())){
                    return false;
                }
            }
        }
        return true;
    }

    public void clearFilter(){
        mFilterColor = COLOR_NONE;
        mFilterLabel.clear();
        mFilterImage = false;
        mFilterCheckbox = false;
        mValuesFilted = mValues;
        notifyDataSetChanged();
    }

    public void clearFilterLabels(){
        mFilterLabel.clear();
        filter();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Note mNote;
        public final View mView;
        public final TextView mTitleView;
        public final TextView mTime;
        public final ImageView mHasCheckbox;
        public final ImageView mHasImage;
        public final ImageView mHasPinned;
        public final ImageView mHasReminder;
        public final CheckBox mCheckBox;
        public final TextView mContent;

        public ViewHolder(View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.select_note_checkbox);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mSelectedPos[getLayoutPosition()] = b;
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mMode == MODE_NORMAL) {
                        if (mNoteItemClickListener != null) {
                            mNoteItemClickListener.onNoteItemClick(ViewHolder.this);
                        }
                    } else {
                        mCheckBox.setChecked(!mCheckBox.isChecked());
                    }
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mMode == MODE_NORMAL) {
                        if (mOnItemLongClickListener != null)
                            return mOnItemLongClickListener.onItemLongClick(ViewHolder.this, getLayoutPosition());
                    } else {
                        mCheckBox.setChecked(!mCheckBox.isChecked());
                        return true;
                    }
                    return false;
                }
            });
            mView = view;
            mTitleView = view.findViewById(R.id.title);
            mTime = view.findViewById(R.id.time);
            mHasCheckbox = view.findViewById(R.id.hasCheckbox);
            mHasImage = view.findViewById(R.id.hasImage);
            mHasPinned = view.findViewById(R.id.hasPin);
            mHasReminder = view.findViewById(R.id.hasReminder);
            mContent = view.findViewById(R.id.content);
            mNote = null;
        }

        public long getNoteId(int position){
            return mValuesFilted.get(position).getId();
        }

        public void bind(Note note){
            mNote = note;
            if(note.getTitle().isEmpty()){
                note.setTitle(mView.getContext().getString(R.string.none_text));
            }
            mTitleView.setText(note.getTitle());
            Calendar calendar = note.getTime_last_update();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String time = dateFormat.format(calendar.getTime());
            String content = "";
            int indexOfEndLine = note.getFull_text().indexOf("\n");
            if(indexOfEndLine != -1){
                content = note.getFull_text().substring(indexOfEndLine + 1);
            }
            mTime.setText(mView.getContext().getString(R.string.last_modify, time));
            mView.setBackgroundTintList(ColorStateList.valueOf(note.getColor()));
            mHasCheckbox.setVisibility(note.isHas_checkbox() ? View.VISIBLE : View.GONE);
            mHasImage.setVisibility(note.isHas_image() ? View.VISIBLE : View.GONE);
            mHasPinned.setVisibility(note.isIs_pinned() ? View.VISIBLE : View.GONE);
            mHasReminder.setVisibility(false ? View.VISIBLE : View.GONE);
            mCheckBox.setVisibility(mMode == MODE_NORMAL ? View.GONE : View.VISIBLE);
            mCheckBox.setChecked(mSelectedPos[getLayoutPosition()]);
            mContent.setText("");
            mContent.setText(content);
        }
    }

    private NoteItemClickListener mNoteItemClickListener;

    public void setNoteItemClickListener(NoteItemClickListener listener){
        mNoteItemClickListener = listener;
    }

    public interface NoteItemClickListener{
        public void onNoteItemClick(ViewHolder viewHolder);
    }
}