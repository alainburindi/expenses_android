
package com.example.expenses;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.OkHttpClient;

public class SignupActivity extends AppCompatActivity {
    @BindView(R.id.input_username) EditText usernameEdit;
    @BindView(R.id.input_email) EditText emailEdit;
    @BindView(R.id.input_password) EditText passwordEdit;
    @BindView(R.id.input_confirm_Password) EditText confirmPasswordEdit;
    private String username, email, password, confirmPassword;

    @BindView(R.id.link_login) TextView test;

    private static final String baseURL = "https://expenses-stagging.herokuapp.com/expenses/";
    OkHttpClient okHttpClient;
    ApolloClient apolloClient;
    CreateUserMutation createUserMutation;

    public static final String PREFERENCES_KEY = "MyACESSKeyTO";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        okHttpClient = new OkHttpClient.Builder().build();
        apolloClient = ApolloClient.builder()
                .serverUrl(baseURL)
                .okHttpClient(okHttpClient)
                .build();
        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
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
        if (areInputsValid()){
            createAccount();
            usernameEdit.setText("should wait");
        }
    }

    private void createAccount() {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Creating the account");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        String TAG = "RESPONSE";
        Toast.makeText(this, "creating", Toast.LENGTH_SHORT).show();
        createUserMutation = CreateUserMutation.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
        apolloClient.mutate(createUserMutation)
                .enqueue(new ApolloCall.Callback<CreateUserMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CreateUserMutation.Data> response) {

                            SignupActivity.this.runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    progress.cancel();
                                    if (response.hasErrors()){
                                        applyError(response.errors().get(0).message());
                                    }else{
//                                       start the login activity here
                                        Log.e(TAG, response.data().createUser.user().toString());
                                    }
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
        message =message
                .replace("[", "")
                .replace("]", "")
                .replace("'", "");
        if(message.contains("email"))
            SignupActivity.this.emailEdit.setError(message);
        else if(message.contains("username"))
            SignupActivity.this.usernameEdit.setError(message);
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
