
package com.example.expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.expenses.utils.Helper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class SignupActivity extends AppCompatActivity {
    @BindViews({R.id.input_username, R.id.input_email, R.id.input_password, R.id.input_confirm_Password})
    List<EditText> myEditsList;
    private String username, email, password, confirmPassword;

    CreateUserMutation createUserMutation;


    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sharedPreferences = getSharedPreferences(Helper.PREFERENCES_KEY, Context.MODE_PRIVATE);
        ButterKnife.bind(this);
    }

    @OnTextChanged({R.id.input_password, R.id.input_confirm_Password})
    void onTextChange() {
        myEditsList.get(2).setError(null);
        myEditsList.get(3).setError(null);
    }

    @OnClick(R.id.btn_signup)
    void sigunp() {
        username = myEditsList.get(0).getText().toString();
        email = myEditsList.get(1).getText().toString();
        password = myEditsList.get(2).getText().toString();
        confirmPassword = myEditsList.get(3).getText().toString();
        validateInputs();
        if (Helper.areInputsValid(myEditsList))
            createAccount();
    }

    private void createAccount() {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Creating the account");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        String TAG = "RESPONSE";
        createUserMutation = CreateUserMutation.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
        Helper.apolloClient.mutate(createUserMutation)
                .enqueue(new ApolloCall.Callback<CreateUserMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CreateUserMutation.Data> response) {
                        SignupActivity.this.runOnUiThread(() -> {
                            progress.cancel();
                            if (response.hasErrors()) {
                                applyError(response.errors().get(0).message());
                            } else {
//                                       start the login activity here
                                goToLogin();
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                });
    }

    private void applyError(String message) {
        message = message
                .replace("[", "")
                .replace("]", "")
                .replace("'", "");
        if (message.contains("email"))
            SignupActivity.this.myEditsList.get(1).setError(message);
        else if (message.contains("username"))
            SignupActivity.this.myEditsList.get(0).setError(message);
    }

    private void validateInputs() {
        if (username.isEmpty())
            myEditsList.get(0).setError("The username is required");
        if (email.isEmpty())
            myEditsList.get(1).setError("The email is required");
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            myEditsList.get(1).setError("The email is not valid");
        if (password.isEmpty())
            myEditsList.get(2).setError("The password is required");
        else if (!Helper.isPasswordValid(password))
            myEditsList.get(2).setError("Password must have at least 8 characters, " +
                    "a number and a capital letter");
        if (confirmPassword.isEmpty())
            myEditsList.get(3).setError("The confirm password is required");
        else if (!password.equals(confirmPassword)) {
            myEditsList.get(2).setError("The password and confirm password do not match");
            myEditsList.get(3).setError("The password and confirm password do not match");
        }
    }

    @OnClick(R.id.link_login)
    void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
