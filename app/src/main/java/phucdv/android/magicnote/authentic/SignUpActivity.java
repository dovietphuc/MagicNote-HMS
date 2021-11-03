package phucdv.android.magicnote.authentic;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.sync.DataSyncReceiver;

public class SignUpActivity extends AppCompatActivity {
    private EditText email, password, repeatPassword;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.sign_up_email);
        password = findViewById(R.id.new_password);
        repeatPassword = findViewById(R.id.repeat_new_password);
        progressBar = findViewById(R.id.change_pwd_progress_bar);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signUp(View view) {
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String rePassword = repeatPassword.getText().toString().trim();

        if (Email.isEmpty() || Password.isEmpty() || !rePassword.contentEquals(Password)) {
            Toast.makeText(this, getString(R.string.invalid_inputs), Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            try {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_successful), Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent();
                                    intent.setAction(DataSyncReceiver.ACTION_SYNC_UP);
                                    intent.setComponent(new ComponentName(SignUpActivity.this, DataSyncReceiver.class));
                                    sendBroadcast(intent);
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    Toast.makeText(SignUpActivity.this,
                                            task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }

    public void openLoginActivity(View view) {
        finish();
        startActivity(new Intent(this,LoginActivity.class));
    }
}
