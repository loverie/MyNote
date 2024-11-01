package com.example.mynote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.animation.ObjectAnimator;
import androidx.appcompat.app.AppCompatActivity;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerButton;
    private ImageView logo;
    private boolean success=false;
    private ProgressBar loadingSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        logo = findViewById(R.id.logo);
        logo.setVisibility(View.VISIBLE);
        logo.setAlpha(0f); // 透明度设置为0
        loadingSpinner = findViewById(R.id.loading_spinner);


        // 创建透明度动画
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
        fadeIn.setDuration(3000); // 动画持续时间为1秒
        fadeIn.start(); // 启动动画
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_text);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 登录逻辑
                loadingSpinner.setVisibility(View.VISIBLE);
                usernameEditText.setEnabled(false);
                passwordEditText.setEnabled(false);
                loginButton.setEnabled(false);
                registerButton.setEnabled(false);
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginUser(username, password);

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
    private void loginUser(String username, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://0975-202-113-189-209.ngrok-free.app/login"); // 后端登录接口
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    // 创建 JSON 数据
                    JSONObject json = new JSONObject();
                    json.put("username", username);
                    json.put("password", password);

                    // 发送请求
                    OutputStream os = conn.getOutputStream();
                    os.write(json.toString().getBytes("UTF-8"));
                    os.close();

                    // 处理响应
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // 结束登录界面
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }



}

