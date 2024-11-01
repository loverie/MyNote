package com.example.mynote;

public class TodoItem {
    private String content;
    private String expectedCompletionTime;

    public TodoItem(String content, String expectedCompletionTime) {
        this.content = content;
        this.expectedCompletionTime = expectedCompletionTime;
    }

    public String getContent() {
        return content;
    }

    public String getExpectedCompletionTime() {
        return expectedCompletionTime;
    }
}