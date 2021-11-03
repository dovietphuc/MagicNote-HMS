package phucdv.android.magicnote.widget;

import static android.app.PendingIntent.*;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.TaskStackBuilder;

import phucdv.android.magicnote.MagicNoteActivity;
import phucdv.android.magicnote.R;
import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.util.Constants;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link PinNoteWidgetConfigureActivity PinNoteWidgetConfigureActivity}
 */
public class PinNoteWidget extends AppWidgetProvider {
    public static final String EXTRA_APPWIDGET_ID = "phucdv.extra.appwidget_id";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Note notePref = PinNoteWidgetConfigureActivity.loadNotePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.pin_note_widget);

        // template to handle the click listener for each item
        Intent clickIntentTemplate = new Intent(context, MagicNoteActivity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);

        Intent listIntent = new Intent(context, PinNoteWidgetService.class);
        listIntent.putExtra(Constants.ARG_PARENT_ID, notePref.getId());
        listIntent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId);
        listIntent.setData(Uri.parse(listIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.widget_list, listIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            PinNoteWidgetConfigureActivity.deleteNoteIdPref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}