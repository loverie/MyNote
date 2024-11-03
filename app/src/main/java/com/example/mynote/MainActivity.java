package com.example.mynote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.animation.ObjectAnimator;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView todoList;
    private TextView dateText, pendingTasksText;
    private FloatingActionButton addTaskButton;
    private List<TodoItem> todoItems = new ArrayList<>();
    private TodoAdapter todoAdapter; // 创建适配器
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateText = findViewById(R.id.date_text);
        pendingTasksText = findViewById(R.id.pending_tasks_text);
        todoList = findViewById(R.id.todo_list);
        addTaskButton = findViewById(R.id.add_task_button);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadTodoItems);
        // 设置RecyclerView
        todoList.setLayoutManager(new LinearLayoutManager(this));
        // 这里可以设置适配器
        // todoList.setAdapter(new TodoAdapter(yourData));
        todoAdapter = new TodoAdapter(todoItems);
        todoList.setLayoutManager(new LinearLayoutManager(this));
        todoList.setAdapter(todoAdapter);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadTodoItems(); // 刷新数据
        });
        // 更新当前日期和未做事项
        updateDateAndPendingTasks();
        addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });
        loadTodoItems();
    }
    private void stopRefreshing() {
        runOnUiThread(() -> {
            swipeRefreshLayout.setRefreshing(false); // 停止刷新动画
        });
    }
    private void updateDateAndPendingTasks() {
        // 获取当前日期和星期
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE"); // 获取星期几的名称

        String currentDate = dateFormat.format(calendar.getTime());
        String currentWeekday = weekFormat.format(calendar.getTime());

        // 更新TextView
        dateText.setText(currentDate + " (" + currentWeekday + ")");

        // 假设未做事项为0，可以在这里调用后端获取未做事项的数量
        updatePendingTasksCount();
    }
    private void updatePendingTasksCount() {
        new Thread(() -> {
            try {
                int userId = UserSession.getInstance().getUserId(); // 获取存储的用户 ID
                URL url = new URL("https://0975-202-113-189-209.ngrok-free.app/pending_tasks?user_id=" + userId); // 添加用户 ID 作为查询参数
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");

                // 处理响应
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // 解析 JSON
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    int pendingTasksCount = jsonResponse.getInt("pending_tasks");

                    // 更新UI，必须在主线程中
                    runOnUiThread(() -> pendingTasksText.setText("未做事项: " + pendingTasksCount));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void loadTodoItems() {
        new Thread(() -> {
            try {
                int userId = UserSession.getInstance().getUserId(); // 从登录信息中获取用户ID
                URL url = new URL("https://0975-202-113-189-209.ngrok-free.app/user_pending_tasks?user_id=" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");

                // 处理响应
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // 解析 JSON
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray pendingTasks = jsonResponse.getJSONArray("pending_tasks");

                    // 更新UI，必须在主线程中
                    runOnUiThread(() -> {
                        todoItems.clear(); // 清空旧数据
                        for (int i = 0; i < pendingTasks.length(); i++) {
                            JSONObject taskObject = null;
                            try {
                                taskObject = pendingTasks.getJSONObject(i);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            String contentText = null;
                            try {
                                contentText = taskObject.getString("content_text");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            String expectedCompletionTime = null;
                            try {
                                expectedCompletionTime = taskObject.getString("expected_completion_time");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            todoItems.add(new TodoItem(contentText, expectedCompletionTime));
                        }

                        // 更新 RecyclerView
                        todoAdapter.notifyDataSetChanged();

                        // 更新当前日期和未做事项
                        updateDateAndPendingTasks();

                        // 停止刷新
                        stopRefreshing();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                stopRefreshing(); // 在异常情况下也要停止刷新
            }
        }).start();
    }
}