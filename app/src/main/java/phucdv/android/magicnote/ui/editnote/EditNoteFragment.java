package phucdv.android.magicnote.ui.editnote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.phucdvb.drawer.DrawerActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.adapter.ColorPickerAdapter;
import phucdv.android.magicnote.adapter.EditNoteItemRecyclerViewAdapter;
import phucdv.android.magicnote.alarm.AlarmReceiver;
import phucdv.android.magicnote.data.checkboxitem.CheckboxItem;
import phucdv.android.magicnote.data.imageitem.ImageItem;
import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.data.textitem.TextItem;
import phucdv.android.magicnote.noteinterface.AsyncResponse;
import phucdv.android.magicnote.noteinterface.ShareComponents;
import phucdv.android.magicnote.ui.colorpicker.ColorPickerDialog;
import phucdv.android.magicnote.ui.datetimepicker.DateTimePickerDialog;
import phucdv.android.magicnote.util.AsyncTaskUtil;
import phucdv.android.magicnote.util.Constants;
import phucdv.android.magicnote.util.FileHelper;
import phucdv.android.magicnote.util.KeyBoardController;

public class EditNoteFragment extends Fragment implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private final int TAKE_PHOTO_REQUEST_CODE = 101;
    private final int SELECT_PHOTO_REQUEST_CODE = 102;
    private final int HAND_DRAWER_REQUEST_CODE = 103;

    protected RecyclerView mRecyclerView;
    protected TextView mTxtTimeCreate;
    protected TextView mTxtTimeUpdate;
    protected TextView mTxtOwner;
    protected EditNoteItemRecyclerViewAdapter mAdapter;
    protected EditNoteViewModel mViewModel;
    protected ShareComponents mShareComponents;
    protected Note mNote;
    protected boolean mIsDelete = false;
    protected Observer<List<TextItem>> mListTextItemObserver = new Observer<List<TextItem>>() {
        @Override
        public void onChanged(List<TextItem> textItems) {
            mAdapter.addValues((List) textItems, TextItem.class);
        }
    };

    protected Observer<List<CheckboxItem>> mListCheckboxItemObserver = new Observer<List<CheckboxItem>>() {
        @Override
        public void onChanged(List<CheckboxItem> checkboxItems) {
            mAdapter.addValues((List) checkboxItems, CheckboxItem.class);
        }
    };

    protected Observer<List<ImageItem>> mListImageItemObserver = new Observer<List<ImageItem>>() {
        @Override
        public void onChanged(List<ImageItem> imageItems) {
            mAdapter.addValues((List) imageItems, ImageItem.class);
        }
    };

    protected Observer<Long> mParentIdObserver = new Observer<Long>() {
        @Override
        public void onChanged(Long aLong) {
            mViewModel.initBaseItemRepository();
            mViewModel.getNote().observe(getViewLifecycleOwner(), new Observer<Note>() {
                @Override
                public void onChanged(Note note) {
                    if(note == null) {
                        return;
                    }
                    mNote = note;
                    mViewModel.setCurrentColor(note.getColor());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm");

                    mTxtTimeCreate.setVisibility(View.VISIBLE);
                    Calendar calendar = note.getTime_create();
                    String time = dateFormat.format(calendar.getTime());
                    mTxtTimeCreate.setText(getString(R.string.time_created, time));

                    mTxtTimeUpdate.setVisibility(View.VISIBLE);
                    calendar = note.getTime_last_update();
                    time = dateFormat.format(calendar.getTime());
                    mTxtTimeUpdate.setText(getString(R.string.last_modify, time));

                    if(note.getUser_name() != null){
                        mTxtOwner.setText(getString(R.string.creator, note.getUser_name()));
                        mTxtOwner.setVisibility(View.VISIBLE);
                    }

                    mViewModel.setIsPinned(note.isIs_pinned());
                    mViewModel.setIsArchive(note.isIs_archive());
                    mViewModel.setIsTrash(note.isIs_deleted());
                    Menu menu = mShareComponents.getToolbar().getMenu();
                    if(menu != null) {
                        MenuItem item = menu.findItem(R.id.action_completely_delete);
                        if (item != null)
                            item.setVisible(true);
                    }
                }
            });
            mViewModel.getListTextItems().observe(getViewLifecycleOwner(), mListTextItemObserver);
            mViewModel.getListCheckboxItems().observe(getViewLifecycleOwner(), mListCheckboxItemObserver);
            mViewModel.getListImageItems().observe(getViewLifecycleOwner(), mListImageItemObserver);
        }
    };

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 41;
    private static final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 42;

    public boolean checkPermission(String permission) {
        int result = getContext().checkSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(String[] permissions, int requestCode) {
        try {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_note_fragment, container, false);

        mTxtTimeCreate = view.findViewById(R.id.tvTimeCreate);
        mTxtTimeUpdate = view.findViewById(R.id.tvTimeUpdate);
        mTxtOwner = view.findViewById(R.id.tvOwner);

        mRecyclerView = view.findViewById(R.id.edit_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAdapter = new EditNoteItemRecyclerViewAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            int downPosX = 0;
            int downPosY = 0;
            int slop = 50;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (Math.abs(downPosX - event.getX()) < slop && Math.abs(downPosY - event.getY()) < slop) {
                        if (mAdapter.getAdapterList().size() == 0
                                || !(mAdapter.getAdapterList().get(mAdapter.getAdapterList().size() - 1) instanceof TextItem)) {
                            mAdapter.addTextItem("");
                        } else {
                            mAdapter.focusOnPosition(mAdapter.getAdapterList().size() - 1);
                        }
                        downPosX = 0;
                        downPosY = 0;
                        return true;
                    }
                    downPosX = 0;
                    downPosY = 0;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downPosX = (int) event.getX();
                    downPosY = (int) event.getY();
                }
                return false;
            }
        });

        mViewModel = new ViewModelProvider(this).get(EditNoteViewModel.class);

        mViewModel.getParentId().observe(getViewLifecycleOwner(), mParentIdObserver);

        mViewModel.getCurrentColor().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer color) {
                view.setBackgroundColor(color);
            }
        });

        mViewModel.getIsPinned().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isPinned) {
                MenuItem item = mShareComponents.getToolbar().getMenu().findItem(R.id.action_pin);
                if(item != null){
                    item.setIcon(isPinned ? R.drawable.ic_unpin : R.drawable.ic_baseline_push_pin_24);
                    item.setTitle(isPinned ? R.string.unpin : R.string.pin);
                }
            }
        });

        Observer<Boolean> stateObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Menu menu = mShareComponents.getToolbar().getMenu();
                if(menu != null){
                    MenuItem archive = menu.findItem(R.id.action_to_archive);
                    MenuItem trash = menu.findItem(R.id.action_to_trash);
                    MenuItem restore = menu.findItem(R.id.action_restore);
                    if(archive != null)
                        archive.setVisible(!mViewModel.getIsArchive().getValue());
                    if (trash != null)
                        trash.setVisible(!mViewModel.getIsTrash().getValue());
                    if(restore != null)
                        restore.setVisible(mViewModel.getIsArchive().getValue() || mViewModel.getIsTrash().getValue());
                }
            }
        };

        mViewModel.getIsArchive().observe(getViewLifecycleOwner(), stateObserver);
        mViewModel.getIsTrash().observe(getViewLifecycleOwner(), stateObserver);

        mShareComponents = (ShareComponents) getContext();
        if (getArguments() != null) {
            long parentId = getArguments().getLong(Constants.ARG_PARENT_ID, Constants.UNKNOW_PARENT_ID);
            if (parentId == Constants.UNKNOW_PARENT_ID) {
                mAdapter.addValues(new ArrayList<>(), Object.class);
            } else {
                mViewModel.getParentId().setValue(parentId);
            }
        }

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getContext() != null && getActivity() != null && getContext() instanceof ShareComponents) {
            mShareComponents.getFloatingActionButton().setOnClickListener(this);
            mShareComponents.getBottomAppBar().replaceMenu(R.menu.edit_bottom_menu);
            mShareComponents.getBottomAppBar().setOnMenuItemClickListener(this);
            mShareComponents.getBottomAppBarTitle().setVisibility(View.GONE);
            mShareComponents.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mShareComponents.setFabDrawable(R.drawable.avd_done_to_add);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_top_menu, menu);
        mShareComponents.getToolbar().setOnMenuItemClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(!mIsDelete) {
            mViewModel.onSave(mAdapter, mNote);
        }
        if (getContext() instanceof ShareComponents) {
            mShareComponents.setFabDrawable(R.drawable.avd_add_to_done);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (getActivity() == null) return;
                if (!KeyBoardController.hideKeyboard(getActivity())) {
                    getActivity().onBackPressed();
                } else {
                    mShareComponents.getBottomAppBar().performShow();
                }
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_color:
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
                colorPickerDialog.setExitsColor(mViewModel.getCurrentColor().getValue());
                colorPickerDialog.setOnColorPickerListener(new ColorPickerAdapter.OnColorPickerListener() {
                    @Override
                    public void onColorPicked(int color) {
                        mViewModel.setCurrentColor(color);
                    }

                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
                colorPickerDialog.showDialog((AppCompatActivity) getActivity());
                return true;
            case R.id.action_reminder:
                DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(getContext());
                dateTimePickerDialog.setDateTimePickerDialogListener(new DateTimePickerDialog.DateTimePickerDialogListener() {
                    @Override
                    public void onDatePicked(DatePicker v, int day, int month, int year) {

                    }

                    @Override
                    public void onTimePicked(TimePicker v, int hour, int min) {

                    }

                    @Override
                    public void onConfirm(DateTimePickerDialog dialog, int day, int month, int year, int hour, int min) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.clear();
                        calendar.set(year, month, day, hour, min, 0);
                        Intent intent = new Intent(getContext(), AlarmReceiver.class);
                        intent.setAction(AlarmReceiver.ACTION_SET_UP_ALARM);
                        intent.putExtra(AlarmReceiver.EXTRA_TIME_REMINDER, calendar.getTimeInMillis());
                        getActivity().sendBroadcast(intent);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancel(DateTimePickerDialog dialog, int day, int month, int year, int hour, int min) {
                        dialog.dismiss();
                    }
                });
                dateTimePickerDialog.showDialog((AppCompatActivity) getActivity());
                return true;
            case R.id.action_pin:
                mViewModel.setIsPinned(!mViewModel.getIsPinned().getValue());
                return true;
            case R.id.action_to_archive:
                mViewModel.setIsArchive(true);
                mViewModel.setIsTrash(false);
                return true;
            case R.id.action_to_trash:
                mViewModel.setIsTrash(true);
                mViewModel.setIsArchive(false);
                return true;
            case R.id.action_restore:
                mViewModel.setIsTrash(false);
                mViewModel.setIsArchive(false);
                return true;
            case R.id.action_completely_delete:
                if(getActivity() != null) {
                    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext())
                            .setTitle(R.string.warning)
                            .setMessage(R.string.warning_delete)
                            .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mIsDelete = true;
                                    mViewModel.deleteNote(mViewModel.getParentId().getValue());
                                    getActivity().onBackPressed();
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
                }
                return true;
            case R.id.action_undo:
                mAdapter.undo();
                return true;
            case R.id.action_redo:
                mAdapter.redo();
                return true;
            case R.id.action_add_check_item:
                mAdapter.addCheckItem("", false);
                return true;
            case R.id.action_add_photo:
                if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        || !checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            READ_STORAGE_PERMISSION_REQUEST_CODE | WRITE_STORAGE_PERMISSION_REQUEST_CODE);
                    return true;
                }

                String[] option = {getString(R.string.camera), getString(R.string.gallery), getString(R.string.hand_drawing)};
                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.add_photo_from)
                        .setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent;
                                switch (which) {
                                    case 0:
                                        mImageUrl = takePicture();
                                        break;
                                    case 1:
                                        intent = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        intent.setType("image/*");
                                        getActivity().startActivityFromFragment(
                                                EditNoteFragment.this,
                                                intent,
                                                SELECT_PHOTO_REQUEST_CODE);
                                        break;
                                    case 2:
                                        getActivity().startActivityFromFragment(EditNoteFragment.this,
                                                new Intent(getContext(), DrawerActivity.class), HAND_DRAWER_REQUEST_CODE);
                                        break;
                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
        }
        return false;
    }

    private String mImageUrl;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
                mAdapter.addImageItem(FileHelper.photoDir(getContext()) + File.separator + mImageUrl, mAdapter.getFocusPosition() + 1);
            } else if (requestCode == SELECT_PHOTO_REQUEST_CODE) {
                new AsyncTaskUtil.copyAsynTask(getContext(), data.getData(), FileHelper.photoDir(getContext()),
                        FileHelper.createFileName("png"),
                        new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                mAdapter.addImageItem((String) output, mAdapter.getFocusPosition() + 1);
                            }
                        }).execute();
            } else if (requestCode == HAND_DRAWER_REQUEST_CODE) {
                mAdapter.addImageItem(data.getExtras().getString(DrawerActivity.EXTRA_HAND_DRAWER, "")
                        , mAdapter.getFocusPosition() + 1);
            }
        }
    }

    private String takePicture() {
        String filename = FileHelper.createFileName("png");
        String dir = FileHelper.photoDir(getContext());
        FileHelper.mkdir(dir);
        String path = dir + File.separator + filename;
        File file = new File(path);
        Uri imageUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName()
                + ".provider", file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        getActivity().startActivityFromFragment(EditNoteFragment.this,
                Intent.createChooser(intent, getString(R.string.take_picture_from)), TAKE_PHOTO_REQUEST_CODE);
        return filename;
    }
}