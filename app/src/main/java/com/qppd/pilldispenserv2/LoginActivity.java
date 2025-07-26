package com.qppd.pilldispenserv2;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.qppd.pilldispenserv2.Libs.AutoTimez.AutotimeClass;
import com.qppd.pilldispenserv2.Libs.Firebasez.FirebaseAuthHelper;
import com.qppd.pilldispenserv2.Libs.Functionz.FunctionClass;
import com.qppd.pilldispenserv2.Libs.Permissionz.PermissionClass;
import com.qppd.pilldispenserv2.Libs.Validatorz.ValidatorClass;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private FunctionClass function = new FunctionClass(this);
    private AutotimeClass autotime = new AutotimeClass(this);
    private PermissionClass permission = new PermissionClass(this, this);

    private int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.INTERNET,
            android.Manifest.permission.CHANGE_NETWORK_STATE};


    private EditText email;
    private EditText password;

    private Button btn_login;

    private TextView txt_signup;

    private FirebaseAuthHelper firebaseAuthHelper = new FirebaseAuthHelper();
    private FirebaseDatabase firebaseDatase = FirebaseDatabase.getInstance();

    private Button btnLogin;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        function.noActionBar(getSupportActionBar());
        autotime.checkAutotime();

        if (!permission.hasPermissions()) {
            permission.requestPermissions(PERMISSION_ALL);
        }

        firebaseAuthHelper.logout();
        initializeComponents();
    }

    private void attemptSignin() {
        // Reset errors.
        email.setError(null);
        password.setError(null);

        boolean cancel = false;
        View focusView = null;

        String signin_email = email.getText().toString();
        String signin_password = password.getText().toString();

        if (TextUtils.isEmpty(signin_email)) {
            email.setError("Email is empty!");
            focusView = email;
            cancel = true;
        } else if (!ValidatorClass.validateEmailOnly(signin_email)) {
            email.setError("Invalid Email!");
            focusView = email;
            cancel = true;
        }
        if (TextUtils.isEmpty(signin_password)) {
            password.setError("Password is empty!");
            focusView = password;
            cancel = true;
        } else if (!ValidatorClass.validatePasswordNoSymbolOnly(
                signin_password)) {
            password.setError("Invalid Password!");
            focusView = password;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            firebaseAuthHelper.login(signin_email, signin_password, new FirebaseAuthHelper.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    String uid = Objects.requireNonNull(user).getUid();
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    
                    // Redirect to Dashboard
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    void initializeComponents() {
        email = findViewById(R.id.email);
        email.setText("sajedhm@gmail.com");
        password = findViewById(R.id.password);
        password.setText("Jedtala01");
        btn_login = findViewById(R.id.login);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                attemptSignin();
                break;
        }
    }


}