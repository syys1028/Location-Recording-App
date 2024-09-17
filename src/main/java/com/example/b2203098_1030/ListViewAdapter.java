package com.example.b2203098_1030;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> listViewItems = new ArrayList<ListViewItem>();
    public void updateItem(int index, ListViewItem element){
        listViewItems.set(index, element);
    }
    public void deleteItem(int index){
        listViewItems.remove(index);
    }
    public ListViewAdapter() {

    }

    public void addItem(Integer numID, String locDate, String locTime, Double latitude, Double longitude, String place, Integer timeSpent) {
        ListViewItem item = new ListViewItem();
        item.setNum(numID);
        item.setLatitude(latitude);
        item.setLongitude(longitude);
        item.setLocDate(locDate);
        item.setLocTime(locTime);
        item.setPlace(place);
        item.setTimeSpent(timeSpent);
        listViewItems.add(item);
    }
    public void clearItems() {
        listViewItems.clear();
    }

    @Override
    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public ListViewItem getItem(int position) {
        return listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView textView1 = (TextView) convertView.findViewById(R.id.id_textview);     // 텍스트뷰에 데이터 세팅
        TextView textView2 = (TextView) convertView.findViewById(R.id.date_textview);
        TextView textView3 = (TextView) convertView.findViewById(R.id.time_textview);
        TextView textView4 = (TextView) convertView.findViewById(R.id.lat_textview);
        TextView textView5 = (TextView) convertView.findViewById(R.id.long_textview);
        TextView textView6 = (TextView) convertView.findViewById(R.id.place_textview);

        ListViewItem item = listViewItems.get(position);
        textView1.setText(String.valueOf(item.getNum()));
        textView2.setText(item.getLocDate());
        textView3.setText(item.getLocTime());
        textView4.setText(String.valueOf(item.getLatitude()));
        textView5.setText(String.valueOf(item.getLongitude()));
        textView6.setText(item.getPlace());
        item.getTimeSpent();
        return convertView;
    }
}
