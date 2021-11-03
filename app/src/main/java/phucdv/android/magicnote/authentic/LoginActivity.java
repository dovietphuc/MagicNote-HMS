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

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.login_progress_bar);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void Login(View view) {
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();

        if (Email.isEmpty() || Password.isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_inputs), Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            firebaseAuth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            try {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent();
                                    intent.setAction(DataSyncReceiver.ACTION_SYNC);
                                    intent.setComponent(new ComponentName(LoginActivity.this, DataSyncReceiver.class));
                                    sendBroadcast(intent);
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    Toast.makeText(LoginActivity.this,
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

    public void forgetPassword(View view) {
        startActivity(new Intent(this,ResetPasswordActivity.class));
    }

    public void signUpNow(View view) {
        finish();
        startActivity(new Intent(this,SignUpActivity.class));
    }
}
