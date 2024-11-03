package com.example.mynote;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TaskDetailActivity extends AppCompatActivity {

    private EditText taskInput, expectedCompletionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail); // 创建一个布局文件

        taskInput = findViewById(R.id.task_input);
        expectedCompletionInput = findViewById(R.id.expected_completion_input);
        Button saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> {
            String taskContent = taskInput.getText().toString();
            String expectedCompletionTime = expectedCompletionInput.getText().toString();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("task_content", taskContent);
            resultIntent.putExtra("expected_completion_time", expectedCompletionTime);
            setResult(RESULT_OK, resultIntent);
            finish(); // 结束当前 Activity
        });
    }
}