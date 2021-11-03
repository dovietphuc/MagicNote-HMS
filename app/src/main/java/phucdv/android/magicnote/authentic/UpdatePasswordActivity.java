package phucdv.android.magicnote.authentic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.sync.DataSyncReceiver;

public class UpdatePasswordActivity extends AppCompatActivity {

    private FirebaseUser mFirebaseUser;
    private EditText mOldPwd;
    private EditText mNewPwd;
    private EditText mRepeatNewPwd;
    private ProgressBar mProgressBar;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
        if(mFirebaseUser == null){
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        mOldPwd = findViewById(R.id.old_password);
        mNewPwd = findViewById(R.id.new_password);
        mRepeatNewPwd = findViewById(R.id.repeat_new_password);
        mProgressBar = findViewById(R.id.change_pwd_progress_bar);
    }

    public void change(View v){
        String oldPwd = mOldPwd.getText().toString().trim();
        String newPwd = mNewPwd.getText().toString().trim();
        String reNewPwd = mRepeatNewPwd.getText().toString().trim();

        if (oldPwd.isEmpty() || newPwd.isEmpty() || !reNewPwd.contentEquals(newPwd)) {
            Toast.makeText(this, getString(R.string.invalid_inputs), Toast.LENGTH_SHORT).show();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            AuthCredential credential = EmailAuthProvider
                    .getCredential(mFirebaseUser.getEmail(), oldPwd);

            // Prompt the user to re-provide their sign-in credentials
            mFirebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFirebaseUser.updatePassword(newPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mProgressBar.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Toast.makeText(getApplicationContext(), getString(R.string.password_update), Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            mProgressBar.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                mProgressBar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void finish(View v){
        finish();
    }
}