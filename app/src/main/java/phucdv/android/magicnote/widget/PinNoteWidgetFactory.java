package phucdv.android.magicnote.widget;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.util.Linkify;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.SortedList;

import com.bumptech.glide.Glide;

import java.util.List;

import phucdv.android.magicnote.MagicNoteActivity;
import phucdv.android.magicnote.R;
import phucdv.android.magicnote.data.BaseItem;
import phucdv.android.magicnote.data.BaseItemRepository;
import phucdv.android.magicnote.data.checkboxitem.CheckboxItem;
import phucdv.android.magicnote.data.imageitem.ImageItem;
import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.data.noteitem.NoteRepository;
import phucdv.android.magicnote.data.textitem.TextItem;
import phucdv.android.magicnote.util.Constants;

public class PinNoteWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private long mNoteId;
    private int mWidgetId;
    private BaseItemRepository mBaseItemRepository;
    private NoteRepository mNoteRepository;
    private SortedList<BaseItem> mSortedList = new SortedList<>(BaseItem.class, new SortedList.Callback<BaseItem>() {
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

    public PinNoteWidgetFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        mWidgetId = intent.getIntExtra(PinNoteWidget.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mNoteId = intent.getLongExtra(Constants.ARG_PARENT_ID, Constants.UNKNOW_PARENT_ID);
        mBaseItemRepository = new BaseItemRepository((Application) applicationContext, mNoteId);
        mBaseItemRepository.getListTextItems().observeForever(new Observer<List<TextItem>>() {
            @Override
            public void onChanged(List<TextItem> textItems) {
                mSortedList.clear();
                addValues((List)textItems);
                if(mBaseItemRepository.getListCheckboxItems() != null && mBaseItemRepository.getListCheckboxItems().getValue() != null){
                    addValues((List)mBaseItemRepository.getListCheckboxItems().getValue());
                }
                if(mBaseItemRepository.getListImageItems() != null && mBaseItemRepository.getListImageItems().getValue() != null){
                    addValues((List)mBaseItemRepository.getListImageItems().getValue());
                }
            }
        });
        mBaseItemRepository.getListImageItems().observeForever(new Observer<List<ImageItem>>() {
            @Override
            public void onChanged(List<ImageItem> imageItems) {
                mSortedList.clear();
                addValues((List)imageItems);
                if(mBaseItemRepository.getListCheckboxItems() != null && mBaseItemRepository.getListCheckboxItems().getValue() != null){
                    addValues((List)mBaseItemRepository.getListCheckboxItems().getValue());
                }
                if(mBaseItemRepository.getListTextItems() != null && mBaseItemRepository.getListTextItems().getValue() != null){
                    addValues((List)mBaseItemRepository.getListTextItems().getValue());
                }
            }
        });
        mBaseItemRepository.getListCheckboxItems().observeForever(new Observer<List<CheckboxItem>>() {
            @Override
            public void onChanged(List<CheckboxItem> checkboxItems) {
                mSortedList.clear();
                addValues((List)checkboxItems);
                if(mBaseItemRepository.getListTextItems() != null && mBaseItemRepository.getListTextItems().getValue() != null){
                    addValues((List)mBaseItemRepository.getListTextItems().getValue());
                }
                if(mBaseItemRepository.getListImageItems() != null && mBaseItemRepository.getListImageItems().getValue() != null){
                    addValues((List)mBaseItemRepository.getListImageItems().getValue());
                }
            }
        });

        mNoteRepository = new NoteRepository((Application) applicationContext);
        mNoteRepository.initNote(mNoteId);
        mNoteRepository.getNote().observeForever(new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                if(note != null) {
                    RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.pin_note_widget);
                    views.setInt(R.id.widget_container, "setBackgroundColor", note.getColor());
                    AppWidgetManager.getInstance(mContext).updateAppWidget(mWidgetId, views);
                }
            }
        });
    }

    public void addValues(List<BaseItem> items) {
        mSortedList.addAll(items);
        AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mWidgetId, R.id.widget_list);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mSortedList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(position == AdapterView.INVALID_POSITION) {
            return null;
        }

        RemoteViews rv = null;
        BaseItem item = mSortedList.get(position);
        Intent intent = new Intent(mContext, MagicNoteActivity.class);
        intent.setAction(MagicNoteActivity.ACTION_NEW_NOTE);
        intent.putExtra(Constants.ARG_PARENT_ID, mNoteId);
        if(item instanceof TextItem) {
            rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_text_item);
            rv.setTextViewText(R.id.text_item, ((TextItem) item).getContent());
            rv.setOnClickFillInIntent(R.id.text_item_container, intent);
        } else if(item instanceof ImageItem){
            rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_image_item);
            try {
                Bitmap bitmap = Glide.with(mContext)
                        .asBitmap()
                        .load(((ImageItem) item).getPath())
                        .submit(512, 512)
                        .get();

                rv.setImageViewBitmap(R.id.image_item, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            rv.setOnClickFillInIntent(R.id.image_item_container, intent);
        } else if(item instanceof CheckboxItem){
            rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_checkbox_item);
            rv.setImageViewResource(R.id.checkbox_img,
                    ((CheckboxItem) item).isIs_checked() ? R.drawable.ic_baseline_check_box_black_24
                    : R.drawable.ic_baseline_check_box_uncheck_black_24);
            rv.setTextViewText(R.id.text_item, ((CheckboxItem) item).getContent());
            rv.setOnClickFillInIntent(R.id.checkbox_container, intent);
        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return mSortedList.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
