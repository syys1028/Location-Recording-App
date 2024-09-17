package com.example.b2203098_1030;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchView extends AppCompatActivity {
    MainActivity.myDBHelper myhelper;
    SQLiteDatabase sqlDB;
    Integer choiceMenu;
    TextView textView;
    EditText editText;
    Button startBtn, backBtn;
    ListView serachList;
    ArrayList<String> items = new ArrayList<String>();
    ArrayAdapter adapter;
    LinearLayout searchLayout;
    String sql, dateText, placeText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_data);

        Intent intent = getIntent();
        choiceMenu = intent.getIntExtra("Choose", 0);
        textView = (TextView) findViewById(R.id.textView4);
        editText = (EditText) findViewById(R.id.editText);
        startBtn = (Button) findViewById(R.id.startBtn);
        backBtn = (Button) findViewById(R.id.backBtn2);
        searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
        serachList = (ListView) findViewById(R.id.searchList);
        items = new ArrayList<String>();
        searchLayout.setVisibility(View.GONE);
        myhelper = new MainActivity.myDBHelper(this);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        if (choiceMenu == 2){
            textView.setText("❗ 날짜를 입력하세요. ex)2023-11-01");
        } else {
            textView.setText("❗ 장소를 입력하세요. ex)군산시");
        }

        backBtn.setOnClickListener(new View.OnClickListener() {     // 뒤로 가기 버튼
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {    // 확인 버튼
            @Override
            public void onClick(View view) {
                items.clear();
                if (choiceMenu == 2){       // 날짜 입력 후 일치하는지 확인
                    dateText = editText.getText().toString();
                    Log.d("DateText", dateText);
                    sql = "SELECT * FROM locHistTable WHERE locDate = '" + dateText + "'";
                } else {                // 검색어 입력 후 포함하는 항목 있는지 확인
                    placeText = editText.getText().toString();
                    sql = "SELECT * FROM locHistTable WHERE place LIKE '%" + placeText + "%'";
                }
                sqlDB = myhelper.getWritableDatabase();
                Cursor cursor = sqlDB.rawQuery(sql, null);
                searchLayout.setVisibility(View.VISIBLE);
                while (cursor.moveToNext()) {
                    String item = cursor.getInt(0) + ". " + cursor.getString(1) + " " + cursor.getString(2)  + " " + cursor.getString(5);
                    items.add(item);
                }
                if (items.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "일치하는 항목이 없습니다.", Toast.LENGTH_LONG).show();
                }
                serachList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                cursor.close();
                sqlDB.close();
            }
        });
    }
}
