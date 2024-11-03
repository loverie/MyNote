package com.example.mynote;

import android.widget.EditText;

public class Task {
    private String taskContent;
    private String expectedCompletionTime;

    public Task(String taskContent, String expectedCompletionTime) {
        this.taskContent = taskContent;
        this.expectedCompletionTime = expectedCompletionTime;
    }

    public String getTaskContent() {
        return taskContent; // 返回任务内容的字符串
    }

    public String getExpectedCompletionTime() {
        return expectedCompletionTime; // 返回预计完成时间的字符串
    }
}