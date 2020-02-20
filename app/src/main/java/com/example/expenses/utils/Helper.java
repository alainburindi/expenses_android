package com.example.expenses.utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import java.util.List;
import java.util.regex.Pattern;

public class Helper {

    private static final String baseURL = "https://expenses-stagging.herokuapp.com/expenses/";


    public static boolean isPasswordValid(String password){
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");

        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean areInputsValid(List<EditText> views){
        for (EditText view: views)
            if (view.getError() != null)
                return false;
        return true;
    }
}
