
package com.example.expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class SignupActivity extends AppCompatActivity {
    @BindView(R.id.input_username) EditText usernameEdit;
    @BindView(R.id.input_email) EditText emailEdit;
    @BindView(R.id.input_password) EditText passwordEdit;
    @BindView(R.id.input_confirm_Password) EditText confirmPasswordEdit;
    private String username, email, password, confirmPassword;

    @BindView(R.id.link_login) TextView test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }

    @OnTextChanged({R.id.input_password, R.id.input_confirm_Password})
    void onTextChange(){
        passwordEdit.setError(null);
        confirmPasswordEdit.setError(null);
    }

    @OnClick(R.id.btn_signup) void sigunp(){
        username = usernameEdit.getText().toString();
        email = emailEdit.getText().toString();
        password = passwordEdit.getText().toString();
        confirmPassword = confirmPasswordEdit.getText().toString();
        validateInputs();
        if (areInputsValid())
            createAccount();
    }

    private void createAccount() {
        Toast.makeText(this, "creating", Toast.LENGTH_SHORT).show();
    }

    private void validateInputs() {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");

        if(username.isEmpty())
            usernameEdit.setError("The username is required");
        if(email.isEmpty())
            emailEdit.setError("The email is required");
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            emailEdit.setError("The email is not valid");
        if(password.isEmpty())
            passwordEdit.setError("The password is required");
        else if(!PASSWORD_PATTERN.matcher(password).matches())
            passwordEdit.setError("Password must have at least 8 characters, a number and a capital letter");
        if(confirmPassword.isEmpty())
            confirmPasswordEdit.setError("The confirm password is required");
        else if (!password.equals(confirmPassword)){
            passwordEdit.setError("The password and confirm password do not match");
            confirmPasswordEdit.setError("The password and confirm password do not match");
        }
    }

    private boolean areInputsValid(){
        return (usernameEdit.getError() == null) &&
                (emailEdit.getError() == null) &&
                (passwordEdit.getError() == null) &&
                (confirmPasswordEdit.getError() == null);
    }
}
