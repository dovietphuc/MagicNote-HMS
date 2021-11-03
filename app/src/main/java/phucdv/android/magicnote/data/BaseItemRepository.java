package phucdv.android.magicnote.data;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import phucdv.android.magicnote.data.checkboxitem.CheckboxItem;
import phucdv.android.magicnote.data.checkboxitem.CheckboxItemDao;
import phucdv.android.magicnote.data.imageitem.ImageItem;
import phucdv.android.magicnote.data.imageitem.ImageItemDao;
import phucdv.android.magicnote.data.textitem.TextItem;
import phucdv.android.magicnote.data.textitem.TextItemDao;
import phucdv.android.magicnote.noteinterface.AsyncResponse;
import phucdv.android.magicnote.util.AsyncTaskUtil;
import phucdv.android.magicnote.util.FileHelper;

public class BaseItemRepository {
    private TextItemDao mTextItemDao;
    private CheckboxItemDao mCheckboxItemDao;
    private ImageItemDao mImageItemDao;
    private LiveData<List<TextItem>> mListTextItem;
    private LiveData<List<CheckboxItem>> mListCheckboxItem;
    private LiveData<List<ImageItem>> mListImageItem;

    public BaseItemRepository(Application application){
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        mTextItemDao = db.textItemDao();
        mCheckboxItemDao = db.checkboxItemDao();
        mImageItemDao = db.imageItemDao();

    }

    public BaseItemRepository(Application application, long parent_id){
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        mTextItemDao = db.textItemDao();
        mCheckboxItemDao = db.checkboxItemDao();
        mImageItemDao = db.imageItemDao();
        mListTextItem = mTextItemDao.getTextItemForParentId(parent_id);
        mListCheckboxItem = mCheckboxItemDao.getCheckboxItemForParentId(parent_id);
        mListImageItem = mImageItemDao.getImageItemsByParentId(parent_id);
    }

    public LiveData<List<TextItem>> getListTextItems(){
        return mListTextItem;
    }

    public LiveData<List<CheckboxItem>> getListCheckboxItems(){
        return mListCheckboxItem;
    }

    public LiveData<List<ImageItem>> getListImageItems(){
        return mListImageItem;
    }

    public void insertTextItem(TextItem textItem){
        new AsyncTaskUtil.insertTextItemAsyncTask(mTextItemDao, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
            }
        }).execute(textItem);
    }

    public void insertAllTextItems(List<TextItem> textItems){
        new AsyncTaskUtil.insertAllTextItemsAsyncTask(mTextItemDao, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
            }
        }).execute(textItems);
    }

    public void insertCheckboxItem(CheckboxItem checkboxItem){
        new AsyncTaskUtil.insertChecboxItemAsyncTask(mCheckboxItemDao, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
            }
        }).execute(checkboxItem);
    }

    public void insertAllCheckboxItems(List<CheckboxItem> checkboxItems){
        new AsyncTaskUtil.insertAllCheckboxItemsAsyncTask(mCheckboxItemDao, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
            }
        }).execute(checkboxItems);
    }

    public void deleteTextByParentId(long parentId){
        new AsyncTaskUtil.deleteTextItemByParentIdAsyncTask(mTextItemDao).execute(parentId);
    }

    public void deleteTextById(long id){
        new AsyncTaskUtil.deleteTextItemByIdAsyncTask(mTextItemDao).execute(id);
    }

    public void deleteCheckboxByParentId(long parentId){
        new AsyncTaskUtil.deleteCheckboxItemByParentIdAsyncTask(mCheckboxItemDao).execute(parentId);
    }

    public void deleteCheckboxById(long id){
        new AsyncTaskUtil.deleteCheckboxItemByIdAsyncTask(mCheckboxItemDao).execute(id);
    }

    public void updateTextItem(TextItem item){
        new AsyncTaskUtil.updateTextItemAsyncTask(mTextItemDao).execute(item);
    }

    public void updateListTextItem(List<TextItem> items){
        new AsyncTaskUtil.updateListTextItemAsyncTask(mTextItemDao).execute(items);
    }

    public void updateCheckboxItem(CheckboxItem item){
        new AsyncTaskUtil.updateCheckboxItemAsyncTask(mCheckboxItemDao).execute(item);
    }

    public void updateListCheckboxItem(List<CheckboxItem> items){
        new AsyncTaskUtil.updateListCheckboxItemAsyncTask(mCheckboxItemDao).execute(items);
    }

    public void insertImageItem(ImageItem imageItem){
        new AsyncTaskUtil.insertImageItemAsyncTask(mImageItemDao, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {

            }
        }).execute(imageItem);
    }

    public void deleteImageItemById(long id) {
        new AsyncTaskUtil.deleteImageItemByIdAsyncTask(mImageItemDao, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
            }
        }).execute(id);
    }

    public void deleteImageItemByParentId(long parentId) {
        new AsyncTaskUtil.deleteImageItemByParentIdAsyncTask(mImageItemDao, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
            }
        }).execute(parentId);
    }

    public void updateImageItem(ImageItem imageItem){
        new AsyncTaskUtil.updateImageItemAsyncTask(mImageItemDao, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {

            }
        }).execute(imageItem);
    }

    public LiveData<ImageItem> getImageItemById(long id){
        return mImageItemDao.getImageItemById(id);
    }
}
