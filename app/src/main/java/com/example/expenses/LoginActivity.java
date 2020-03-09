package com.example.expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.expenses.utils.Helper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private String usernameOREmail, password;

    @BindViews({R.id.input_username_login, R.id.input_password_login})
    List<EditText> myEditsList;

    private SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPreferences =
                getSharedPreferences(Helper.PREFERENCES_KEY, Context.MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.link_signup)
    void goToSignup() {
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }

    @OnClick(R.id.btn_login)
    void login() {
        password = myEditsList.get(1).getText().toString();
        usernameOREmail = myEditsList.get(0).getText().toString();
        validateInputs();
        if (Helper.areInputsValid(myEditsList))
            authenticateUser();
    }

    private void authenticateUser() {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Authenticating");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        String TAG = "LOGIN";
        LoginUserMutation loginUserMutation = LoginUserMutation.builder()
                .email(usernameOREmail)
                .username(usernameOREmail)
                .password(password)
                .build();
        Helper.apolloClient.mutate(loginUserMutation)
                .enqueue(new ApolloCall.Callback<LoginUserMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<LoginUserMutation.Data> response) {
                        LoginActivity.this.runOnUiThread(() -> {
                            progress.cancel();
                            if (response.hasErrors()) {
                                Toast.makeText(LoginActivity.this, response.errors().get(0)
                                        .message(), Toast.LENGTH_LONG).show();
                            } else {
                                assert response.data() != null;
                                assert response.data().loginUser != null;
                                setUserData(response.data().loginUser);
                            }
                        });

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                });

    }

    private void setUserData(LoginUserMutation.LoginUser loginUserData) {
        preferencesEditor.putString("authToken", loginUserData.authToken()).commit();
    }

    private void validateInputs() {
        if (usernameOREmail.isEmpty()) {
            myEditsList.get(0).setError("The username or the email is required");
        }
        if (password.isEmpty())
            myEditsList.get(2).setError("The password is required");
        else if (!Helper.isPasswordValid(password))
            myEditsList.get(2).setError("Password must have at least 8 characters, a number and " +
                    "a capital letter");
    }
}
