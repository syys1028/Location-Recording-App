package com.example.b2203098_1030;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class RouteView extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap gMap;
    LatLng latLng;
    SupportMapFragment mapFragment;
    PolylineOptions polylineOptions;
    ArrayList<LatLng> arrayList;
    ArrayList<String> placeList, timeSpentList;
    MainActivity.myDBHelper myhelper;
    SQLiteDatabase sqlDB;
    Integer selectedNum;
    double latitude = 37.566668, longitude = 126.978378;    // 초기 위치 : 서울 시청
    boolean isAllRoute = false;
    String dateText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_view);
        // 위치 권한
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityResultLauncher<String[]> permissionResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> { });
            permissionResult.launch((new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}));
        }

        Intent intent = getIntent();
        selectedNum = intent.getIntExtra("ClickNo", 0);
        isAllRoute = intent.getBooleanExtra("AllRoute", false);
        dateText = intent.getStringExtra("dateTexts");

        myhelper = new MainActivity.myDBHelper(this);
        arrayList = new ArrayList<LatLng>();
        placeList = new ArrayList<String>();
        timeSpentList = new ArrayList<String>();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sqlDB = myhelper.getReadableDatabase();

        if (isAllRoute == false) {                                      // 데이터 하나만 클릭한 경우
            String sql = "select * from locHistTable where numID = " + selectedNum;
            Cursor cursor = sqlDB.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                latitude = cursor.getDouble(3);
                longitude = cursor.getDouble(4);
                //int timeSpent = cursor.getInt(6);
                //Toast.makeText(getApplicationContext(), latitude + "," + longitude + "," + timeSpent, Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            latLng = new LatLng(latitude, longitude);
        } else {                                                        // 전체 경로보기 클릭한 경우
            String sql = "select * from locHistTable where locDate = '" + dateText + "'";
            Cursor cursor = sqlDB.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                if (dateText.equals(cursor.getString(1))){
                    arrayList.add(new LatLng(cursor.getDouble(3), cursor.getDouble(4)));
                    placeList.add(cursor.getString(5));
                    timeSpentList.add(String.valueOf(cursor.getInt(6)));
                }
            }
            cursor.close();
            if (arrayList.isEmpty()){                   // 경로가 비어있는 경우 서울 시청 표시
                arrayList.add(new LatLng(latitude, longitude));
                placeList.add("대한민국 서울특별시 중구 세종대로 110");
                timeSpentList.add("0");
            }
            latLng = arrayList.get(0);
        }
        sqlDB.close();

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        polylineOptions = new PolylineOptions();        // 경로 표시
        polylineOptions.color(Color.WHITE);
        polylineOptions.width(5);
        polylineOptions.addAll(arrayList);
        gMap.addPolyline(polylineOptions);

        for (int i=0; i<arrayList.size(); i++){         // 마커 찍고 텍스트 세팅
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            markerOptions.title(placeList.get(i).substring(5));
            markerOptions.snippet("머문 시간 : " + timeSpentList.get(i) + "분");
            markerOptions.position(arrayList.get(i));
            gMap.addMarker(markerOptions);
        }

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));     // 카메라 이동

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

            }
        });
    }
}
