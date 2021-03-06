package com.example.expenses.utils;

import android.widget.EditText;

import com.apollographql.apollo.ApolloClient;

import java.util.List;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;

public class Helper {

    private static final String baseURL = "https://expenses-stagging.herokuapp.com/expenses/";

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
    public static ApolloClient apolloClient = ApolloClient.builder()
            .serverUrl(baseURL)
            .okHttpClient(okHttpClient)
            .build();


    public static final String PREFERENCES_KEY = "MyACESSKeyTO";

    public static boolean isPasswordValid(String password) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");

        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean areInputsValid(List<EditText> views) {
        for (EditText view : views)
            if (view.getError() != null)
                return false;
        return true;
    }

}
