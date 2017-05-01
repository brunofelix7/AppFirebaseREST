package com.example.appfirebaserest.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.appfirebaserest.R;
import com.example.appfirebaserest.model.Solicitation;

/**
 * Classe do adapter do meu ListView
 */
public class MyAdapter extends BaseAdapter {

    //  Arrays
    private HashMap<String, Solicitation> data;

    //  Layouts
    private Activity activity;
    private TextView tv_urgency, tv_nivel_consciencia, tv_nivel_respiracao, tv_status, tv_date;
    private static LayoutInflater inflater = null;

    public MyAdapter(Activity a, HashMap<String, Solicitation> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(convertView == null) {
            view = inflater.inflate(R.layout.list_item, null);
        }
        tv_urgency = (TextView) view.findViewById(R.id.tv_urgency);
        tv_nivel_consciencia = (TextView) view.findViewById(R.id.tv_nivel_consciencia);
        tv_nivel_respiracao = (TextView) view.findViewById(R.id.tv_nivel_respiracao);
        tv_status = (TextView) view.findViewById(R.id.tv_status);
        tv_date = (TextView) view.findViewById(R.id.tv_date);

        List<String> p = new ArrayList<>();
        HashMap<String, Solicitation> s = new HashMap<>();
        for (Map.Entry<String, Solicitation> entry : data.entrySet()) {
            p.add(entry.getKey());
            s.put(entry.getKey(), entry.getValue());
        }

        Solicitation value = s.get(p.get(position));
        tv_urgency.setText(value.getUrgency());
        tv_nivel_consciencia.setText(value.getNivel_consciencia());
        tv_nivel_respiracao.setText(value.getNivel_respiracao());
        tv_status.setText(value.getStatus());
        tv_date.setText(value.getDate());

        return view;
    }
}