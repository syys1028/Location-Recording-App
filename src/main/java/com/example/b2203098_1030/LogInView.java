package com.example.b2203098_1030;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LogInView extends AppCompatActivity {
    EditText idlogin, pwlogin;
    Button loginBtn, gosignbtn;
    TextView failLogin;
    Handler handler = new Handler();
    ArrayList<Member> memberArrayList = new ArrayList<>();
    String inputID, inputPW;
    Integer index = 0, okCode = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        idlogin = (EditText) findViewById(R.id.idLogin);
        pwlogin = (EditText) findViewById(R.id.pwLogin);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        gosignbtn = (Button) findViewById(R.id.goSignBtn);
        failLogin = (TextView) findViewById(R.id.failLogin);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputID = idlogin.getText().toString();
                inputPW = pwlogin.getText().toString();
                // 회원가입
                if (inputID.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    final String urlStr = "https://syys10280.iwinv.net/member/json.php";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            requesturl(urlStr);
                        }
                    }).start();
                }

            }
        });
        gosignbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inIntent = new Intent(getApplicationContext(), SignUpView.class);
                startActivity(inIntent);
            }
        });
    }
    public void requesturl(String urlStr){
        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection != null) {
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                int resCode = connection.getResponseCode();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                String line = null;
                while (true) {
                    line = reader.readLine();
                    if (line == null) break;
                    output.append(line + "\n");
                }
                reader.close();
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        println(output.toString());
        // output
    }

    public void println(String data){
        handler.post(new Runnable() {
            @Override
            public void run() {
                //textView.setText(data);
                jsonParsing(data);
            }
        });
    }

    private void jsonParsing(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray memberArray = jsonObject.getJSONArray("Member");
            memberArrayList.clear();
            for (int i = 0; i < memberArray.length(); i++){
                JSONObject memberObject = memberArray.getJSONObject(i);

                Member member = new Member();
                member.setMemberId(memberObject.getString("id"));
                member.setMemberPw(memberObject.getString("pw"));
                if (inputID.equals(memberObject.getString("id"))) {
                    index = i;
                }
                Log.d("aaa", index.toString());

                memberArrayList.add(member);
                checkID(index);
            }
            if (okCode == 2 || okCode == 3) Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkID(int i) {
        if (inputID.equals(memberArrayList.get(i).getMemberId())) {
            if (inputPW.equals(memberArrayList.get(i).getMemberPw())) {
                // 로그인 성공
                if (okCode != 1){
                    Constants.memberStaticID = inputID;
                    okCode = 1;
                    Intent outIntent = new Intent(getApplicationContext(), MainActivity.class);
                    outIntent.putExtra("IsOk", okCode);
                    setResult(RESULT_OK, outIntent);
                    finish();
                }
            } else {
                okCode = 2;
            }
        } else {
            okCode = 3;
        }
    }
}