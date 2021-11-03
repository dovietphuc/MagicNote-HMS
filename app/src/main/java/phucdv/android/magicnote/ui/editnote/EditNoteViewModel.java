package phucdv.android.magicnote.ui.editnote;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.SortedList;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.adapter.EditNoteItemRecyclerViewAdapter;
import phucdv.android.magicnote.data.BaseItem;
import phucdv.android.magicnote.data.BaseItemRepository;
import phucdv.android.magicnote.data.checkboxitem.CheckboxItem;
import phucdv.android.magicnote.data.imageitem.ImageItem;
import phucdv.android.magicnote.data.label.Label;
import phucdv.android.magicnote.data.label.LabelRepository;
import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.data.noteitem.NoteRepository;
import phucdv.android.magicnote.data.textitem.TextItem;
import phucdv.android.magicnote.noteinterface.AsyncResponse;
import phucdv.android.magicnote.util.AsyncTaskUtil;
import phucdv.android.magicnote.util.Constants;
import phucdv.android.magicnote.util.FileHelper;

public class EditNoteViewModel extends AndroidViewModel {
    private NoteRepository mNoteRepository;
    private BaseItemRepository mBaseItemRepository;
    private MutableLiveData<Long> mParentId;
    private LiveData<Note> mNote;
    private MutableLiveData<Integer> mCurrentColor;

    private MutableLiveData<Boolean> mIsPinned;
    private MutableLiveData<Boolean> mIsArchive;
    private MutableLiveData<Boolean> mIsTrash;

    private LabelRepository mLabelRepository;

    public EditNoteViewModel(Application application) {
        super(application);
        mNoteRepository = new NoteRepository(application);
        mLabelRepository = new LabelRepository(application);
        mParentId = new MutableLiveData<Long>();
        mCurrentColor = new MutableLiveData<>(application.getColor(R.color.default_note_color));
        mIsPinned = new MutableLiveData<>(false);
        mIsArchive = new MutableLiveData<>(false);
        mIsTrash = new MutableLiveData<>(false);
    }

    public void initBaseItemRepository(){
        mBaseItemRepository = new BaseItemRepository(getApplication(), mParentId.getValue());
        mNoteRepository.initNote(mParentId.getValue());
        mNote = mNoteRepository.getNote();
    }

    public void initBaseItemRepository(long parentId){
        mBaseItemRepository = new BaseItemRepository(getApplication(), parentId);
        mNoteRepository.initNote(parentId);
        mNote = mNoteRepository.getNote();
    }

    public MutableLiveData<Integer> getCurrentColor() {
        return mCurrentColor;
    }

    public void setCurrentColor(int color) {
        this.mCurrentColor.setValue(color);
    }

    public MutableLiveData<Boolean> getIsPinned() {
        return mIsPinned;
    }

    public void setIsPinned(boolean mIsPinned) {
        this.mIsPinned.setValue(mIsPinned);
    }

    public MutableLiveData<Boolean> getIsArchive() {
        return mIsArchive;
    }

    public void setIsArchive(boolean isArchive) {
        this.mIsArchive.setValue(isArchive);
    }

    public MutableLiveData<Boolean> getIsTrash() {
        return mIsTrash;
    }

    public void setIsTrash(boolean isTrash) {
        this.mIsTrash.setValue(isTrash);
    }

    public LiveData<Note> getNote(){
        return mNote;
    }

    public MutableLiveData<Long> getParentId() {
        return mParentId;
    }

    public LiveData<List<TextItem>> getListTextItems() {
        return mBaseItemRepository.getListTextItems();
    }

    public LiveData<List<CheckboxItem>> getListCheckboxItems() {
        return mBaseItemRepository.getListCheckboxItems();
    }

    public LiveData<List<ImageItem>> getListImageItems() {
        return mBaseItemRepository.getListImageItems();
    }

    public void insertNote(Note note) { mNoteRepository.insert(note); }

    public void insertNote(Note note, AsyncResponse response) { mNoteRepository.insert(note, response); }

    public void insertTextItem(TextItem textItem){
        mBaseItemRepository.insertTextItem(textItem);
    }

    public void insertAllTextItems(List<TextItem> textItems){
        mBaseItemRepository.insertAllTextItems(textItems);
    }

    public void insertCheckboxItem(CheckboxItem checkboxItem){
        mBaseItemRepository.insertCheckboxItem(checkboxItem);
    }

    public void insertAllCheckboxItems(List<CheckboxItem> checkboxItems){
        mBaseItemRepository.insertAllCheckboxItems(checkboxItems);
    }

    public void deleteNote(long id){
        mNoteRepository.deleteNote(id);
    }

    public void updateTextItem(TextItem item){
        mBaseItemRepository.updateTextItem(item);
    }

    public void updateListTextItem(List<TextItem> items){
        mBaseItemRepository.updateListTextItem(items);
    }

    public void updateCheckboxItem(CheckboxItem item){
        mBaseItemRepository.updateCheckboxItem(item);
    }

    public void updateListCheckboxItem(List<CheckboxItem> items) {
        mBaseItemRepository.updateListCheckboxItem(items);
    }

    public void updateNote(Note item){
        mNoteRepository.updateNote(item);
    }

    public void updateNote(Note item, AsyncResponse response){
        mNoteRepository.updateNote(item, response);
    }

    public void updateListNote(List<Note> items){
        mNoteRepository.updateListNote(items);
    }

    public void deleteTextItemById(long id){
        mBaseItemRepository.deleteTextById(id);
    }

    public void deleteCheckboxItemById(long id){
        mBaseItemRepository.deleteCheckboxById(id);
    }

    public void insertImageItem(ImageItem imageItem){
        mBaseItemRepository.insertImageItem(imageItem);
    }

    public void deleteImageItemById(long id){
        mBaseItemRepository.deleteImageItemById(id);
    }

    public void deleteImageItemByParentId(long parentId){
        mBaseItemRepository.deleteImageItemByParentId(parentId);
    }

    public void updateImageItem(ImageItem imageItem){
        mBaseItemRepository.updateImageItem(imageItem);
    }

    public static List<Label> getLabels(String str){
        ArrayList<Label> labels = new ArrayList<>();
        str = str.replaceAll("[^#|ء-يA-Za-z0-9_-]", " ");
        String[] split = str.trim().split(" ");
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        for(String s : split){
            if(s.length() > 1 && s.contains("#")){
                s = s.substring(s.indexOf("#"));
                labels.add(new Label(s.substring(1),
                        firebaseUser != null ? firebaseUser.getUid() : null, true));
            }
        }
        return labels;
    }

    public void onSave(EditNoteItemRecyclerViewAdapter adapter, Note note){
        if(adapter.getAllTextCount() + adapter.getNoneTextItemCount() != 0 || note != null) {
            HashMap<BaseItem, Integer> hashMap = adapter.getHashMap();

            // Bkav PhucDVb: create base note item to insert/update
            Note toInsertNote = (note == null) ? new Note() : note;
            toInsertNote.setColor(mCurrentColor.getValue());
            toInsertNote.setTime_last_update(Calendar.getInstance());
            toInsertNote.setIs_pinned(mIsPinned.getValue());
            toInsertNote.setIs_archive(mIsArchive.getValue());
            toInsertNote.setIs_deleted(mIsTrash.getValue());
            SortedList<BaseItem> listBase = adapter.getAdapterList();

            String fullText = adapter.getAllTextContent();
            List<Label> labels = getLabels(fullText);

            // Bkav PhucDVb: find title for note item
            if(adapter.getAllTextCount() == 0){
                toInsertNote.setTitle("");
            } else {
                toInsertNote.setFull_text(fullText);

                for (int i = 0; i < listBase.size(); i++) {
                    BaseItem item = listBase.get(i);
                    if (item instanceof TextItem) {
                        if (!((TextItem) item).getContent().trim().isEmpty()) {
                            toInsertNote.setTitle(((TextItem) item).getContent());
                            break;
                        }
                    } else if (item instanceof CheckboxItem) {
                        if (!((CheckboxItem) item).getContent().trim().isEmpty()) {
                            toInsertNote.setTitle(((CheckboxItem) item).getContent());
                            break;
                        }
                    } else if (item instanceof ImageItem) {
                    }
                }
            }

            // Bkav PhucDVb: check if has image/checkbox...
            boolean hasCheckbox = false;
            boolean hasImage = false;
            for (int i = 0; i < listBase.size(); i++) {
                BaseItem item = listBase.get(i);
                if (item instanceof CheckboxItem) {
                    hasCheckbox = true;
                } else if (item instanceof ImageItem) {
                    hasImage = true;
                }
            }
            toInsertNote.setHas_checkbox(hasCheckbox);
            toInsertNote.setHas_image(hasImage);

            // Bkav PhucDVb: insert/update note and child item
            if (note == null) {
                toInsertNote.setTime_create(toInsertNote.getTime_last_update());
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    toInsertNote.setUid(user.getUid());
                    toInsertNote.setUser_name(user.getEmail());
                }
                insertNote(toInsertNote, new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        initBaseItemRepository((Long) output);
                        for (int i = 0; i < listBase.size(); i++) {
                            BaseItem item = listBase.get(i);
                            item.setTime_stamp_update(Calendar.getInstance().getTimeInMillis());
                            item.setParent_id((Long) output);
                            if (item instanceof TextItem) {
                                insertTextItem((TextItem) item);
                            } else if (item instanceof CheckboxItem) {
                                insertCheckboxItem((CheckboxItem) item);
                            } else if (item instanceof ImageItem) {
                                insertImageItem((ImageItem) item);
                            }
                        }

                        mLabelRepository.insertAllForNote((LifecycleOwner) adapter.getContext(), (Long) output, labels);
                    }
                });
            } else {
                mLabelRepository.insertAllForNote((LifecycleOwner) adapter.getContext(), mParentId.getValue(), labels);

                updateNote(toInsertNote, new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        initBaseItemRepository(note.getId());
                        for (BaseItem item : hashMap.keySet()) {
                            int state = hashMap.get(item);
                            item.setParent_id(note.getId());
                            item.setTime_stamp_update(Calendar.getInstance().getTimeInMillis());
                            if (state == EditNoteItemRecyclerViewAdapter.STATE_NONE) {
                                continue;
                            } else if (state == EditNoteItemRecyclerViewAdapter.STATE_ADD) {
                                if (item instanceof TextItem) {
                                    insertTextItem((TextItem) item);
                                } else if (item instanceof CheckboxItem) {
                                    insertCheckboxItem((CheckboxItem) item);
                                } else if(item instanceof ImageItem){
                                    insertImageItem((ImageItem) item);
                                }
                            } else if (state == EditNoteItemRecyclerViewAdapter.STATE_MODIFY) {
                                if (item instanceof TextItem) {
                                    updateTextItem((TextItem) item);
                                } else if (item instanceof CheckboxItem) {
                                    updateCheckboxItem((CheckboxItem) item);
                                } else if (item instanceof ImageItem){
                                    updateImageItem((ImageItem) item);
                                }
                            } else if (state == EditNoteItemRecyclerViewAdapter.STATE_DELETE) {
                                if (item instanceof TextItem) {
                                    deleteTextItemById(item.getId());
                                } else if (item instanceof CheckboxItem) {
                                    deleteCheckboxItemById(item.getId());
                                } else if(item instanceof ImageItem){
                                    deleteImageItemById(item.getId());
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}