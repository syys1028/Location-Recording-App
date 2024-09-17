package com.example.b2203098_1030;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    myDBHelper myhelper;
    SQLiteDatabase sqlDB;
    LinearLayout calLayout, listLayout;
    Button backBtn, routeBtn;
    CalendarView calendarView;
    ListView listView;
    public ListViewAdapter adapter;
    TextView dateTextView;
    Integer selectedNo, selectedPos, changeNum = 0;
    String dateText = "a";
    boolean isAllRoute = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("위치 기록 앱");
        calLayout = (LinearLayout) findViewById(R.id.calLayout);
        listLayout = (LinearLayout) findViewById(R.id.listLayout);
        backBtn = (Button) findViewById(R.id.backBtn);
        routeBtn = (Button) findViewById(R.id.routeBtn);
        dateTextView = (TextView) findViewById(R.id.dateTextview);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        listView = (ListView) findViewById(R.id.llistview1);

        calLayout.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.GONE);

        // 위치 권한 설정
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityResultLauncher<String[]> permissionResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(
                        android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if (fineLocationGranted && coarseLocationGranted) {
                    startLocationService();
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            });
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            permissionResult.launch((new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}));
        } else {    // 백그라운드 시작
            startLocationService();
        }


        // 데이터 가져오기
        adapter = new ListViewAdapter();
        listView.setAdapter(adapter);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {      // 캘린더 뷰 데이터 가져오기
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                if (month+1 < 10) {
                    if (day < 10) {
                        dateText = year + "-0" + (month+1) + "-0" + day;
                    } else dateText = year + "-0" + (month+1) + "-" + day;
                } else {
                    if (day < 10) {
                        dateText = year + "-" + (month+1) + "-0" + day;
                    } else dateText = year + "-" + (month+1) + "-" + day;
                }
                dateTextView.setText("✔" + dateText + " 이동 기록");
                changeLayout();
                listViewSetup();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {     // 날짜 재선택 버튼
            @Override
            public void onClick(View view) {
                changeLayout();
            }
        });
        routeBtn.setOnClickListener(new View.OnClickListener() {    // 경로보기 버튼
            @Override
            public void onClick(View view) {
                isAllRoute = true;
                Intent intent = new Intent(getApplicationContext(), RouteView.class);   // RouteView intent
                intent.putExtra("AllRoute", isAllRoute);
                intent.putExtra("dateTexts", dateText);
                startActivity(intent);
            }
        });
        insertData();
    }

    public void insertData(){       // DB 생성
        myhelper = new myDBHelper(this);
        sqlDB = myhelper.getWritableDatabase();
        sqlDB.close();
    }

    public void changeLayout(){     // 캘린더뷰-리스트뷰 토글
        if (changeNum == 0) {
            changeNum = 1;
            calLayout.setVisibility(View.GONE);
            listLayout.setVisibility(View.VISIBLE);
        } else {
            changeNum = 0;
            calLayout.setVisibility(View.VISIBLE);
            listLayout.setVisibility(View.GONE);
        }
    }
    public void listViewSetup(){        // 리스트 뷰 세팅
        sqlDB = myhelper.getReadableDatabase();
        String sql = "select * from locHistTable";
        Cursor cursor = sqlDB.rawQuery(sql, null);
        adapter.clearItems();
        while (cursor.moveToNext()) {       // 데이터 삽입
            //Toast.makeText(getApplicationContext(), dateText + "," + cursor.getString(1) , Toast.LENGTH_SHORT).show();
            if (dateText.equals(cursor.getString(1))){
                adapter.addItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getDouble(3), cursor.getDouble(4), cursor.getString(5), cursor.getInt(6));
            }
        }
        adapter.notifyDataSetChanged();
        cursor.close();
        sqlDB.close();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {     // 리스트 뷰 클릭 시 좌표 보여주기
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewItem item = adapter.getItem(position);
                selectedPos = position;
                selectedNo = item.getNum();
                isAllRoute = false;
                Intent intent = new Intent(getApplicationContext(), RouteView.class);
                intent.putExtra("ClickNo",selectedNo);
                intent.putExtra("AllRoute", isAllRoute);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {     // 메뉴 세팅
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "데이터 초기화");
        SubMenu subMenu = menu.addSubMenu("데이터 검색");
        subMenu.add(0, 2, 0, "날짜 검색");
        subMenu.add(0, 3, 0, "경로 검색");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       // 메뉴 클릭 이벤트
        switch (item.getItemId()){
            case 1:     // 데이터 초기화
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("데이터 초기화");
                dlg.setMessage("초기화 하시겠습니까?");
                dlg.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlDB = myhelper.getWritableDatabase();
                        myhelper.onUpgrade(sqlDB, 1,2);
                        sqlDB.close();
                        adapter.clearItems();
                        adapter.notifyDataSetChanged();
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
                return true;
            case 2:     // 날짜 검색
                Intent intent1 = new Intent(getApplicationContext(), SearchView.class);
                intent1.putExtra("Choose", 2);
                startActivity(intent1);
                return true;
            case 3:     // 경로 (장소 이름) 검색
                Intent intent2 = new Intent(getApplicationContext(), SearchView.class);
                intent2.putExtra("Choose", 3);
                startActivity(intent2);
                return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {       // 위치 권한
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isLocationServiceRunning() {        // 백그라운드 서비스
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {       // 백그라운드 서비스 시작
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            //Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {       // 백그라운드 서비스 종료 - 사용안함
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            //Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }
    public static class myDBHelper extends SQLiteOpenHelper {       // db 설정
        public myDBHelper(Context context) {
            super(context, "locHistTable", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {   // Location History Table, "고유번호/날짜/시간/위도/경도/장소/머문시간"
            String sql = "create table locHistTable (numID integer primary key autoincrement, locDate char(20), " +
                    "locTime char(20), latitude double, longitude double, place char(200), timeSpent integer);";
            Log.d("SQL", sql);
            sqLiteDatabase.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {   // 데이터 삭제
            String sql = "drop table if exists locHistTable";
            sqLiteDatabase.execSQL(sql);
            onCreate(sqLiteDatabase);
        }
    }
}