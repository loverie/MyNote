package com.example.mynote;

import android.widget.EditText;

public class Task {
    private EditText taskInput;
    private EditText expectedCompletionInput;


    public Task(EditText taskInput, EditText expectedCompletionInput) {
        this.taskInput = taskInput;
        this.expectedCompletionInput = expectedCompletionInput;
    }

    public EditText getTaskInput() {
        return taskInput;
    }

    public EditText getExpectedCompletionInput() {
        return expectedCompletionInput;
    }
}