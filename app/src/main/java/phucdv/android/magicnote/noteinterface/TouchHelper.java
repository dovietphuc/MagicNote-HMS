package phucdv.android.magicnote.noteinterface;

import android.graphics.Canvas;

import androidx.recyclerview.widget.RecyclerView;

public interface TouchHelper {
    public void onSwipeLeft(RecyclerView.ViewHolder viewHolder);
    public void onSwipeRight(RecyclerView.ViewHolder viewHolder);
    public boolean onMove(RecyclerView recyclerView,
                       RecyclerView.ViewHolder viewHolder,
                       RecyclerView.ViewHolder target);
    public void onChildDraw(Canvas c , RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState,
                            boolean isCurrentlyActive);
}
