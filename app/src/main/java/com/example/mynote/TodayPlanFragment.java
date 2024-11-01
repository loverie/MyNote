package com.example.mynote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TodayPlanFragment extends Fragment {

    private LinearLayout taskListContainer;
    private Button addTaskButton, completeButton;
    private TextView currentDateText;
    private List<Task> taskList = new ArrayList<>(); // 用于存储任务

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_today_plan_fragment, container, false);

        taskListContainer = view.findViewById(R.id.task_list_container);
        addTaskButton = view.findViewById(R.id.add_task_button);
        completeButton = view.findViewById(R.id.complete_button); // 确保布局中有这个按钮
        currentDateText = view.findViewById(R.id.current_date_text);

        // 显示当前日期和星期
        updateCurrentDate();

        // 添加任务输入框
        addTaskButton.setOnClickListener(v -> addNewTaskInput());

        // 完成按钮点击事件
        completeButton.setOnClickListener(v -> sendTasksToServer());

        return view;
    }

    private void updateCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd (EEEE)", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        currentDateText.setText(currentDate);
    }

    private void addNewTaskInput() {
        // 创建新的输入框和附件按钮的布局
        LinearLayout newTaskInputLayout = new LinearLayout(getActivity());
        newTaskInputLayout.setOrientation(LinearLayout.HORIZONTAL);
        newTaskInputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText taskInput = new EditText(getActivity());
        taskInput.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        taskInput.setHint("输入今日计划内容");

        EditText expectedCompletionInput = new EditText(getActivity());
        expectedCompletionInput.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        expectedCompletionInput.setHint("预计完成时间");

        ImageButton attachButton = new ImageButton(getActivity());
        attachButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        attachButton.setImageResource(android.R.drawable.ic_input_add);
        attachButton.setContentDescription("添加附件");

        newTaskInputLayout.addView(taskInput);
        newTaskInputLayout.addView(expectedCompletionInput);
        newTaskInputLayout.addView(attachButton);

        taskListContainer.addView(newTaskInputLayout);

        // 将任务信息添加到列表中
        taskList.add(new Task(taskInput, expectedCompletionInput)); // 假设 Task 是自定义类
    }

    private void sendTasksToServer() {
        JSONArray tasksArray = new JSONArray();

        for (Task task : taskList) {
            String taskContent = task.getTaskInput().getText().toString();
            String expectedCompletionTime = task.getExpectedCompletionInput().getText().toString();

            if (!taskContent.isEmpty() && !expectedCompletionTime.isEmpty()) {
                JSONObject taskObject = new JSONObject();
                try {
                    taskObject.put("task_content", taskContent);
                    taskObject.put("expected_completion_time", expectedCompletionTime);
                    tasksArray.put(taskObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 发送 JSON 数组到后端
        sendTasksToBackend(tasksArray);
    }

    private void sendTasksToBackend(JSONArray tasksArray) {
        new Thread(() -> {
            try {
                URL url = new URL("https://0975-202-113-189-209.ngrok-free.app/add_task"); // 替换为你的后端 URL
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = tasksArray.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 处理成功响应
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "任务已成功添加", Toast.LENGTH_SHORT).show());
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "添加任务失败", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
