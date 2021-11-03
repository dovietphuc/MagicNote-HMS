package phucdv.android.magicnote.ui.recyclebin;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import phucdv.android.magicnote.R;
import phucdv.android.magicnote.adapter.ColorPickerAdapter;
import phucdv.android.magicnote.data.noteandlabel.NoteWithLabels;
import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.ui.BaseListNoteFragment;
import phucdv.android.magicnote.ui.BaseViewModel;
import phucdv.android.magicnote.ui.colorpicker.ColorPickerDialog;
import phucdv.android.magicnote.util.NoteItemTouchCallback;

public class RecycleBinFragment extends BaseListNoteFragment {

    private RecycleBinViewModel mViewModel;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mItemTouchHelper = new ItemTouchHelper(new NoteItemTouchCallback(ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT, this));
        if (mRecyclerView != null){
            mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        }
        mViewModel = new ViewModelProvider(this).get(RecycleBinViewModel.class);
        mViewModel.init(this);

        mViewModel.getListNotes().observe(getViewLifecycleOwner(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                mAdapter.setValues(notes);
                notifyListChange(notes);
            }
        });

        mViewModel.setCallback(new BaseViewModel.Callback() {
            @Override
            public void queryDoneNoteWithLabels(List<NoteWithLabels> noteWithLabels) {
                mAdapter.setNoteWithLabels(noteWithLabels);
            }
        });
        return view;
    }

    @Override
    public int getMenu() {
        return R.menu.trash_menu;
    }

    public void onSwipeLeft(RecyclerView.ViewHolder viewHolder){
        if(viewHolder.getLayoutPosition() >= mViewModel.getListNotes().getValue().size()){
            return;
        }
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.warning)
                .setMessage(R.string.warning_delete)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Note note = mViewModel.getListNotes().getValue()
                                .get(viewHolder.getLayoutPosition());
                        mViewModel.deleteNote(note.getId());
                        Snackbar.make(getView(), getString(R.string.delete), Snackbar.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.notifyItemChanged(viewHolder.getLayoutPosition());
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void onSwipeRight(RecyclerView.ViewHolder viewHolder){
        if(viewHolder.getLayoutPosition() >= mViewModel.getListNotes().getValue().size()){
            return;
        }
        Note note = mViewModel.getListNotes().getValue()
                .get(viewHolder.getLayoutPosition());
        mViewModel.moveToProcessing(note);
        Snackbar.make(getView(), getString(R.string.move_to_processing), Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewModel.moveToTrash(note);
                    }
                }).show();
    }
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target){
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(viewHolder.getLayoutPosition() >= mViewModel.getListNotes().getValue().size()){
            return;
        }
        new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftLabel(getString(R.string.completely_delete))
                .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_forever_24)
                .addSwipeLeftBackgroundColor(getResources().getColor(R.color.completely_delete))
                .addSwipeRightLabel(getString(R.string.processing))
                .addSwipeRightActionIcon(R.drawable.ic_baseline_emoji_objects_white_24)
                .addSwipeRightBackgroundColor(getResources().getColor(R.color.to_processing))
                .create()
                .decorate();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_select_note:
                mItemTouchHelper.attachToRecyclerView(null);
                mAdapter.startSelect();
                mShareComponents.getToolbar().getMenu().clear();
                mShareComponents.getToolbar().inflateMenu(R.menu.trash_select_menu);
                return true;
            case R.id.action_select_all:
                mItemTouchHelper.attachToRecyclerView(null);
                mAdapter.startSelectAll();
                mShareComponents.getToolbar().getMenu().clear();
                mShareComponents.getToolbar().inflateMenu(R.menu.trash_select_menu);
                return true;
            case R.id.action_clear_trash:
                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.clear_trash)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mViewModel.clearTrash();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            case R.id.action_change_color:
                List<Note> notes = mAdapter.getSelectedList();
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
                colorPickerDialog.setOnColorPickerListener(new ColorPickerAdapter.OnColorPickerListener() {
                    @Override
                    public void onColorPicked(int color) {
                        mViewModel.updateColor(color, notes);
                    }

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        doneSelect();
                    }
                });
                colorPickerDialog.showDialog((AppCompatActivity) getActivity());
                return true;
            case R.id.action_to_processing:
                builder = new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.processing)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mViewModel.moveToProcessing(mAdapter.getSelectedList());
                                Snackbar.make(getView(), getString(R.string.move_to_processing), Snackbar.LENGTH_LONG).show();
                                doneSelect();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog = builder.create();
                alertDialog.show();
                return true;
            case R.id.action_to_archive:
                builder = new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.archive)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mViewModel.moveToArchive(mAdapter.getSelectedList());
                                Snackbar.make(getView(), getString(R.string.move_to_archive), Snackbar.LENGTH_LONG).show();
                                doneSelect();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog = builder.create();
                alertDialog.show();
                return true;
            case R.id.action_pin:
                mViewModel.pin(mAdapter.getSelectedList());
                doneSelect();
                return true;
            case R.id.action_unpin:
                mViewModel.unpin(mAdapter.getSelectedList());
                doneSelect();
                return true;
            case R.id.action_completely_delete:
                builder = new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.warning)
                        .setMessage(R.string.warning_delete)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mViewModel.deleteListNote(mAdapter.getSelectedList());
                                Snackbar.make(getView(), getString(R.string.delete), Snackbar.LENGTH_LONG).show();
                                doneSelect();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog = builder.create();
                alertDialog.show();
                return true;
            case R.id.action_share:
                doneSelect();
                return true;
            case R.id.action_done_select:
                doneSelect();
                return true;
        }
        return super.onMenuItemClick(item);
    }

    public void doneSelect(){
        mAdapter.endSelect();
        mShareComponents.getToolbar().getMenu().clear();
        mShareComponents.getToolbar().inflateMenu(R.menu.trash_menu);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public String[] getPopupMenuItem(Note note) {
        return new String[]{
                getString(R.string.change_color),
                note.isIs_pinned() ? getString(R.string.unpin) : getString(R.string.pin)
//                , getString(R.string.share)
                , getString(R.string.archive)
                , getString(R.string.restore)
                , getString(R.string.completely_delete)};
    }

    @Override
    public void onPopupItemSelect(DialogInterface dialog, int which, Note note) {
        switch (which){
            case 0:
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
                colorPickerDialog.setExitsColor(note.getColor());
                colorPickerDialog.setOnColorPickerListener(new ColorPickerAdapter.OnColorPickerListener() {
                    @Override
                    public void onColorPicked(int color) {
                        mViewModel.updateColor(color, note);
                    }

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });
                colorPickerDialog.showDialog((AppCompatActivity) getActivity());
                break;
            case 1:
                mViewModel.pinOrUnpin(note);
                break;
//            case 2:
//                break;
            case 2:
                mViewModel.moveToArchive(note);
                break;
            case 3:
                mViewModel.moveToProcessing(note);
                break;
            case 4:
                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.warning)
                        .setMessage(R.string.warning_delete)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mViewModel.deleteNote(note.getId());
                                Snackbar.make(getView(), getString(R.string.delete), Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }
    }
}