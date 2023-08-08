package com.example.magacinapp;

import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class ListActivity extends AppCompatActivity {

    String sheetID ="19TFv_FVlWt3kxCe_i3XyvZJT64PPO41XGS0UtJ3bRzI";
    String apiKEY =  "AIzaSyDfQkB7ACKsFgCcbhnQ45hVG1E9Va8pT2k";

    String strNaziv;
    String strOpis;
    String strVlasnik;
    String strBarkod;

    String strSlika;

    JSONArray jsonArray;
    ListView listView;

    CustomAdapter customAdapter;

    ArrayList<String > listNaziv = new ArrayList<String>();
    ArrayList<String > listOpis = new ArrayList<String>();
    ArrayList<String > listVlasnik = new ArrayList<String>();

    ArrayList<String > listSlika = new ArrayList<String>();
    ArrayList<String > listBarkod = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_data);

        listView = findViewById(R.id.listview_id);

        String urls = "https://sheets.googleapis.com/v4/spreadsheets/"+sheetID+"/values/Spisak?key="+apiKEY;

        RequestQueue queue = Volley.newRequestQueue(ListActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urls, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    jsonArray = response.getJSONArray("values");
                } catch (Exception e) {
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    IntStream.range(1, jsonArray.length())
                            .forEach(i -> {
                                try {
                                    JSONArray json = jsonArray.getJSONArray(i);
                                    strBarkod =json.getString(1);
                                    strNaziv = json.getString(2);
                                    strOpis = json.getString(3);
                                    strSlika = json.getString(4);
                                    strVlasnik = json.getString(5);

                                    listBarkod.add(strBarkod);
                                    listNaziv.add(strNaziv);
                                    listOpis.add(strOpis);
                                    listSlika.add(strSlika);
                                    listVlasnik.add(strVlasnik);

                                    customAdapter = new CustomAdapter(getApplicationContext(), listNaziv, listOpis, listVlasnik);
                                    listView.setAdapter(customAdapter);

                                } catch (Exception e) {
                                }


                            });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);

    }

}
