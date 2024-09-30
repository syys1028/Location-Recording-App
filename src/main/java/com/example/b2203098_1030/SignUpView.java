package com.example.b2203098_1030;

import android.os.Bundle;
import android.os.Handler;
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

public class SignUpView extends AppCompatActivity {
    EditText idsign, pwsign;
    Button signBtn, backloginbtn;
    Handler handler = new Handler();
    LogInView logInView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        idsign = (EditText) findViewById(R.id.idSignup);
        pwsign = (EditText) findViewById(R.id.pwSignup);
        signBtn = (Button) findViewById(R.id.signBtn);
        backloginbtn = (Button) findViewById(R.id.backLoginBtn);

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String urlStr = "https://syys10280.iwinv.net/member/insert_ok.php?" + "id="
                        + idsign.getText().toString() + "&pw=" + pwsign.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 요청 처리 함수
                        requesturl(urlStr);
                    }
                }).start();
                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        backloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

            for (int i = 0; i < memberArray.length(); i++){
                JSONObject memberObject = memberArray.getJSONObject(i);

                Member member = new Member();
                member.setMemberId(memberObject.getString("id"));
                member.setMemberPw(memberObject.getString("pw"));

                logInView.memberArrayList.clear();
                logInView.memberArrayList.add(member);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}