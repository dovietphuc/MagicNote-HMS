package phucdv.android.magicnote.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.adapter.NoteItemRecyclerViewAdapter;
import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.databinding.PinNoteWidgetConfigureBinding;
import phucdv.android.magicnote.util.Constants;

/**
 * The configuration screen for the {@link PinNoteWidget PinNoteWidget} AppWidget.
 */
public class PinNoteWidgetConfigureActivity extends AppCompatActivity
        implements NoteItemRecyclerViewAdapter.NoteItemClickListener {

    private static final String PREFS_NAME = "phucdv.android.magicnote.widget.PinNoteWidget";
    private static final String PREF_PREFIX_KEY_ID = "appwidget_id_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private PinNoteWidgetConfigureBinding binding;

    private WidgetConfigViewModel mViewModel;

    private NoteItemRecyclerViewAdapter mAdapter;

    public PinNoteWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveNotePref(Context context, int appWidgetId, Note note) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY_ID + appWidgetId, note.getId());
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static Note loadNotePref(Context context, int appWidgetId) {
        Note note = new Note();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        note.setId(prefs.getLong(PREF_PREFIX_KEY_ID + appWidgetId, Constants.UNKNOW_PARENT_ID));
        return note;
    }

    static void deleteNoteIdPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY_ID + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        binding = PinNoteWidgetConfigureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mViewModel = new ViewModelProvider(this).get(WidgetConfigViewModel.class);
        mViewModel.init(this);

        mAdapter = new NoteItemRecyclerViewAdapter();
        mAdapter.setNoteItemClickListener(this);

        mViewModel.getListNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                mAdapter.setValues(notes);
            }
        });
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(mAdapter);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

    }

    @Override
    public void onNoteItemClick(NoteItemRecyclerViewAdapter.ViewHolder viewHolder) {
        final Context context = PinNoteWidgetConfigureActivity.this;

        // When the button is clicked, store the string locally
        saveNotePref(context, mAppWidgetId, viewHolder.mNote);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        PinNoteWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}