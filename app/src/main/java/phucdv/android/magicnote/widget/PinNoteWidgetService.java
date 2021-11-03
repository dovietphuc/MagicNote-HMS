package phucdv.android.magicnote.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class PinNoteWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PinNoteWidgetFactory(this.getApplicationContext(), intent);
    }
}
