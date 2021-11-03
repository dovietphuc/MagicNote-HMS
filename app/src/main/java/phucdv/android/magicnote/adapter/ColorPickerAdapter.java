package phucdv.android.magicnote.adapter;

import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.ui.colorpicker.ColorPickerDialog;

public class ColorPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int VIEW_TYPE_COLOR = 1;
    private int VIEW_TYPE_NONE = 0;

    private int[] mColors;
    private OnColorPickerListener mOnColorPickerListener;
    private int mExitsColor;
    private RecyclerView.ViewHolder mExitsItem;

    public ColorPickerAdapter(int[] colors){
        mColors = colors;
    }

    public void setExitsColor(int color){
        mExitsColor = color;
    }

    public void setOnColorPickerListener(OnColorPickerListener onColorPickerListener){
        mOnColorPickerListener = onColorPickerListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mColors[position] == ColorPickerDialog.COLOR_NONE ? VIEW_TYPE_NONE : VIEW_TYPE_COLOR;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return (viewType == VIEW_TYPE_COLOR) ? new ColorPickerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_picker_item, parent, false))
                : new ColorPickerNoneItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_picker_none_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ColorPickerViewHolder){
            ((ColorPickerViewHolder)holder).bind(mColors[position]);
        } else if(holder instanceof ColorPickerNoneItemViewHolder){
            ((ColorPickerNoneItemViewHolder)holder).bind(mColors[position]);

        }
    }

    @Override
    public int getItemCount() {
        return mColors.length;
    }

    public class ColorPickerViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private FrameLayout mColorItem;
        private FrameLayout mBackground;

        public ColorPickerViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mBackground = itemView.findViewById(R.id.background);
            mColorItem = itemView.findViewById(R.id.color_item);
            mColorItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mExitsItem != null) {
                        if (mExitsItem.getLayoutPosition() != getLayoutPosition()) {
                            mBackground.setBackgroundColor(Color.BLUE);
                            if (mExitsItem instanceof ColorPickerNoneItemViewHolder) {
                                ((ColorPickerNoneItemViewHolder) mExitsItem).mBackground.setBackgroundColor(Color.WHITE);
                            } else if (mExitsItem instanceof ColorPickerViewHolder) {
                                ((ColorPickerViewHolder) mExitsItem).mBackground.setBackgroundColor(Color.WHITE);
                            }
                            mExitsItem = ColorPickerViewHolder.this;
                            if (mOnColorPickerListener != null) {

                                mOnColorPickerListener.onColorPicked(mColors[getLayoutPosition()]);
                            }
                        }
                    } else {
                        mExitsItem = ColorPickerViewHolder.this;
                        mBackground.setBackgroundColor(Color.BLUE);
                        if (mOnColorPickerListener != null) {
                            mOnColorPickerListener.onColorPicked(mColors[getLayoutPosition()]);
                        }
                    }
                }
            });
        }

        public void bind(int color){
            mColorItem.setBackgroundColor(color);
            if(mExitsColor == color){
                mBackground.setBackgroundColor(Color.BLUE);
                mExitsItem = this;
            } else {
                mBackground.setBackgroundColor(Color.WHITE);
            }
        }
    }

    public class ColorPickerNoneItemViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private ImageView mColorItem;
        private FrameLayout mBackground;

        public ColorPickerNoneItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mBackground = itemView.findViewById(R.id.background);
            mColorItem = itemView.findViewById(R.id.color_item);
            mColorItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mExitsItem != null) {
                        if (mExitsItem.getLayoutPosition() != getLayoutPosition()) {
                            mBackground.setBackgroundColor(Color.BLUE);
                            if (mExitsItem instanceof ColorPickerNoneItemViewHolder) {
                                ((ColorPickerNoneItemViewHolder) mExitsItem).mBackground.setBackgroundColor(Color.WHITE);
                            } else if (mExitsItem instanceof ColorPickerViewHolder) {
                                ((ColorPickerViewHolder) mExitsItem).mBackground.setBackgroundColor(Color.WHITE);
                            }
                            mExitsItem = ColorPickerNoneItemViewHolder.this;
                            if (mOnColorPickerListener != null) {
                                mOnColorPickerListener.onColorPicked(mColors[getLayoutPosition()]);
                            }
                        }
                    } else {
                        mExitsItem = ColorPickerNoneItemViewHolder.this;
                        mBackground.setBackgroundColor(Color.BLUE);
                        if (mOnColorPickerListener != null) {
                            mOnColorPickerListener.onColorPicked(mColors[getLayoutPosition()]);
                        }
                    }
                }
            });
        }

        public void bind(int color){
            if(mExitsColor == color){
                mBackground.setBackgroundColor(Color.BLUE);
                mExitsItem = this;
            } else {
                mBackground.setBackgroundColor(Color.WHITE);
            }
        }
    }

    public interface OnColorPickerListener{
        public void onColorPicked(int color);
        public void onDismiss(DialogInterface dialog);
    }
}
