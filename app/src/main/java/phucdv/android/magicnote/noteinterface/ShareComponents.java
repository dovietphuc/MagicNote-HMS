package phucdv.android.magicnote.noteinterface;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public interface ShareComponents {
    public DrawerLayout getDrawerLayout();
    public FloatingActionButton getFloatingActionButton();
    public NavController getNavController();
    public BottomAppBar getBottomAppBar();
    public TextView getBottomAppBarTitle();
    public AnimatedVectorDrawable getAnimatedVectorDrawable();
    public void setFabDrawable(int drawableId);
    public void navigate(int target, Bundle bundle);
    public Toolbar getToolbar();
}
