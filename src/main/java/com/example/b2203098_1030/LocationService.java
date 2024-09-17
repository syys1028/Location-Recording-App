package com.example.b2203098_1030;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LocationService extends Service {
    MainActivity.myDBHelper myhelper;
    SQLiteDatabase sqlDB;
    Integer timespent = 0;
    private LocationCallback mlocationCallback = new LocationCallback() {
        private Location firstLoc;
        private long firstLocTime;
        private String firstDate;
        float distance;
        long timeDif;
        boolean isFirst = true;
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                Location currentLocation = locationResult.getLastLocation();    // 현재 위치
                long currentTime = System.currentTimeMillis();                  // 현재 시간

                if (firstLoc != null) {         // 초기 상태(first~가 비어있음)가 아닐 경우
                    distance = firstLoc.distanceTo(currentLocation);            // distance는 현재 위치와 초기 위치의 차이 (이동 거리)
                    timeDif = currentTime - firstLocTime;                       // timeDif는 현재 시간 - 초기 시간 (머문 시간)
                    timespent = (int) (timeDif / 60000);
                    if (timeDif >= 10 * 60 * 1000) {                            // 10분 이상 머물렀을 때
                        if (distance > 50) {                                        // 50미터 넘게 이동한 경우, 머문 시간 수정 후 초기화
                            modifyLocDB(timespent);
                            firstLoc = null;
                            //Toast.makeText(getApplicationContext(), "10분 이상, 50미터 벗어남", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isFirst == true){                               // db에 처음 삽입하는 경우
                                insertLocDB(firstLoc.getLatitude(), firstLoc.getLongitude(), firstDate, timespent); // 초기 위치, 날짜 db에 삽입
                                isFirst = false;
                            } else {
                                modifyLocDB(timespent);
                            }
                            //Toast.makeText(getApplicationContext(), "10분 이상, 50미터 못벗어남", Toast.LENGTH_SHORT).show();
                        }
                    } else {                                                    // 머문 시간이 10분 미만일 때
                        if (distance > 50) {                                        // 50미터 넘게 이동하면 초기화
                            //Toast.makeText(getApplicationContext(), "10분 미만, 50미터 벗어남",  Toast.LENGTH_SHORT).show();
                            firstLoc = null;
                        } else {
                            //Toast.makeText(getApplicationContext(), "10분 미만, 50미터 못벗어남", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {                                // 맨 초기 상태이면 가장 최근 위치와 현재 시간 first에 저장, 변수 초기화
                    firstLoc = currentLocation;
                    firstLocTime = currentTime;
                    timeDif = 0;
                    timespent = 0;
                    isFirst = true;
                    Date date = new Date(firstLocTime);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    firstDate = simpleDateFormat.format(date);
                    //Toast.makeText(getApplicationContext(), "초기 상태 시작", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private void insertLocDB(double latitude, double longitude, String dateTimes, Integer timeSpents) {     // 데이터 삽입 함수
        myhelper = new MainActivity.myDBHelper(this);
        sqlDB = myhelper.getWritableDatabase();

        String locDates = dateTimes.substring(0, 10);  // "yyyy-MM-dd"
        String locTimes = dateTimes.substring(11);  // "hh:mm:ss"
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);         // 위도 경도를 한국 도로명 주소로 변환
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String sql = "INSERT INTO locHistTable (locDate, locTime, latitude, longitude, place, timeSpent) " +
                        "VALUES ('" + locDates + "', '" + locTimes + "', " + latitude + ", " + longitude + ", '" + address + "', " + timeSpents + ");";
                sqlDB.execSQL(sql);
                //Toast.makeText(getApplicationContext(), "주소: " + address, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error fetching address", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlDB.close();
    }
    private void modifyLocDB(Integer timeSpents) {              // 데이터 수정 함수
        myhelper = new MainActivity.myDBHelper(this);
        sqlDB = myhelper.getWritableDatabase();

        String lastData = "SELECT * FROM locHistTable ORDER BY numID DESC LIMIT 1;";        // 가장 최근에 들어온 함수를 수정
        Cursor cursor = sqlDB.rawQuery(lastData, null);

        if (cursor.moveToFirst()) {
            int lastItemId = cursor.getInt(0);
            String updateSql = "UPDATE locHistTable SET timeSpent = " + timeSpents + " WHERE numID = " + lastItemId + ";";
            sqlDB.execSQL(updateSql);
        }
        cursor.close();
        sqlDB.close();
    }

    // 백그라운드 위치 서비스 설정
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60 * 1000);                 // 1분에 한번씩 위치 가져옴
        locationRequest.setFastestInterval(60 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mlocationCallback, Looper.getMainLooper());
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mlocationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
