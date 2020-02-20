package com.example.expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expenses.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

//    @BindView(R.id.input_username_login)
//    EditText usernameEdit;
//    @BindView(R.id.input_email_login)
//    EditText emailEdit;
//    @BindView(R.id.input_password_login)
//    EditText passwordEdit;

    private String email, username, password;

    @BindViews({R.id.input_username_login, R.id.input_email_login, R.id.input_password_login})
    List<EditText> myEditsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.link_signup) void goToSignup(){
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }

    @OnClick(R.id.btn_login) void login(){
        password = myEditsList.get(2).getText().toString();
        username = myEditsList.get(0).getText().toString();
        email = myEditsList.get(1).getText().toString();
        validateInputs();
        if(Helper.areInputsValid(myEditsList))
            authenticateUser();
    }

    private void authenticateUser() {
        Toast.makeText(this, "auth", Toast.LENGTH_SHORT).show();
    }

    private void validateInputs() {
        if(username.isEmpty())
            myEditsList.get(0).setError("The username is required");
        if(email.isEmpty())
            myEditsList.get(1).setError("The email is required");
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            myEditsList.get(1).setError("The email is not valid");
        if(password.isEmpty())
            myEditsList.get(2).setError("The password is required");
        else if(!Helper.isPasswordValid(password))
            myEditsList.get(2).setError("Password must have at least 8 characters, a number and a capital letter");
    }
}
