package com.phucdvb.drawer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import com.phucdvb.drawer.GraphicsHelper;
import com.phucdvb.drawer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DrawerView extends View {

    private static final int NONE = -1;
    private static final int LINE = 0;
    private static final int CIRCLE = 1;

    private Paint mPaint;
    private List<Path> mListPaths;
    Stack<Path> mRedoListPath;
    private int mColorPaint;
    private float mBrushSize;

    private float mRadius;

    private int mTouchSlop;
    private float mStartX;
    private float mStartY;
    private float mLastX;
    private float mLastY;
    private Handler mHandler;
    private final Runnable mLongPressRunable = new Runnable() {
        @Override
        public void run() {
            if (GraphicsHelper.isCircle(mPointList)) {
                mListPaths.remove(mListPaths.size() - 1);
                drawCircle(mStartX, mStartY, mRadius);
                mBlockDrawer = true;
                mDragShape = true;
                mCurrentShape = CIRCLE;
            } else if(GraphicsHelper.isLine(mPointList)){
                mListPaths.remove(mListPaths.size() - 1);
                drawLine(mStartX, mStartY, mLastX, mLastY);
                mBlockDrawer = true;
                mDragShape = true;
                mCurrentShape = LINE;
            }
        }
    };
    private static boolean mBlockDrawer = false;
    private static boolean mDragShape = false;
    private static int mCurrentShape = NONE;

    private List<Point> mPointList;

    public DrawerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mListPaths = new ArrayList<>();
        mRedoListPath = new Stack<>();
        mColorPaint = Color.RED;
        mBrushSize = getContext().getResources().getDimension(R.dimen.brushSize);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPain();
        mHandler = new Handler();
        mPointList = new ArrayList<>();
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    private void setupPain() {
        mPaint.setColor(mColorPaint);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mBrushSize);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Path path : mListPaths) {
            canvas.drawPath(path, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        if(mBlockDrawer){
            if(event.getAction() != MotionEvent.ACTION_MOVE){
                mBlockDrawer = false;
                mDragShape = false;
            } else if(mDragShape) {
                if(mCurrentShape == CIRCLE){
                    mListPaths.remove(mListPaths.size() - 1);
                    drawCircle(pointX, pointY, mRadius);
                } else if(mCurrentShape == LINE){
                    float x = pointX - (mLastX - mStartX);
                    float y = pointY - (mLastY - mStartY);
                    mListPaths.remove(mListPaths.size() - 1);
                    drawLine(x , y, x + (mLastX - mStartX), y + (mLastY - mStartY));
                }
                postInvalidate();
                return true;
            }
        }

        mPointList.add(new Point((int) pointX, (int) pointY));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Path path = new Path();
                path.moveTo(pointX, pointY);
                mListPaths.add(path);
                mStartX = pointX;
                mStartY = pointY;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isActualMove(event)) {
                    mHandler.removeCallbacks(mLongPressRunable);
                } else {
                    mHandler.postDelayed(mLongPressRunable, ViewConfiguration.getLongPressTimeout());
                }
                mListPaths.get(mListPaths.size() - 1).lineTo(pointX, pointY);
                mLastX = pointX;
                mLastY = pointY;
                mRadius = Math.max(mRadius, Math.max(Math.abs(mLastX - mStartX), Math.abs(mLastY - mStartY)) / 2);
                break;
            default:
                mRadius = 0;
                mPointList.clear();
                mCurrentShape = NONE;
                mHandler.removeCallbacks(mLongPressRunable);
                return true;
        }
        postInvalidate();
        return true;
    }

    public void clearAll() {
        mRedoListPath.clear();
        mListPaths.clear();
        postInvalidate();
    }

    private boolean isActualMove(MotionEvent event) {
        int dx = (int) (event.getX() - mLastX);
        int dy = (int) (event.getY() - mLastY);
        return Math.abs(dx) > 0 || Math.abs(dy) > 0;
    }

    public void undo() {
        if (!mListPaths.isEmpty()) {
            mRedoListPath.push(mListPaths.remove(mListPaths.size() - 1));
            postInvalidate();
        }
    }

    public void redo() {
        if (!mRedoListPath.isEmpty()) {
            mListPaths.add(mRedoListPath.pop());
            postInvalidate();
        }
    }

    private void drawCircle(float x, float y, float radius){
        Path path = new Path();
        path.addCircle(x, y, radius, Path.Direction.CW);
        mListPaths.add(path);
        postInvalidate();
    }

    private void drawLine(float startX, float startY, float lastX, float lastY){
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(lastX, lastY);
        mListPaths.add(path);
        postInvalidate();
    }
}
