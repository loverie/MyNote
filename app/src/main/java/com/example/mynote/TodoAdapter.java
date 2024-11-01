package com.example.mynote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    private List<TodoItem> todoItems;

    public TodoAdapter(List<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoItem todoItem = todoItems.get(position);
        holder.contentTextView.setText(todoItem.getContent());
        holder.expectedCompletionTextView.setText(todoItem.getExpectedCompletionTime());
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contentTextView, expectedCompletionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.todo_content);
            expectedCompletionTextView = itemView.findViewById(R.id.expected_completion_time);
        }
    }
}