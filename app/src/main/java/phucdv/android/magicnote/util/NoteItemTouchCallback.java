package phucdv.android.magicnote.util;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import phucdv.android.magicnote.noteinterface.TouchHelper;

public class NoteItemTouchCallback extends ItemTouchHelper.SimpleCallback {
    private TouchHelper mTouchHelper;

    public NoteItemTouchCallback(int dragDirs, int swipeDirs, TouchHelper touchHelper) {
        super(dragDirs, swipeDirs);
        mTouchHelper = touchHelper;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return mTouchHelper.onMove(recyclerView, viewHolder, target);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT) {
            mTouchHelper.onSwipeLeft(viewHolder);
        } else {
            mTouchHelper.onSwipeRight(viewHolder);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        mTouchHelper.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
