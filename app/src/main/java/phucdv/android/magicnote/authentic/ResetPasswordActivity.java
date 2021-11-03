package phucdv.android.magicnote.authentic;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import phucdv.android.magicnote.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText editText;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editText=findViewById(R.id.edit_text_resetPassEmail);
        progressBar=findViewById(R.id.progress_bar_reset_password);
        firebaseAuth=FirebaseAuth.getInstance();

    }

    public void sendResetPassword(View view) {
        String email=editText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this,
                    getString(R.string.pls_insert_email), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Toast.makeText(ResetPasswordActivity.this,
                            getString(R.string.reset_pwd_has_been_send),
                            Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Toast.makeText(ResetPasswordActivity.this, task.getException().getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
