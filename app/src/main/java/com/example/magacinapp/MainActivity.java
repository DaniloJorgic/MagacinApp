package com.example.magacinapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.magacinapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {
    String poruka;
    String sheetID ="19TFv_FVlWt3kxCe_i3XyvZJT64PPO41XGS0UtJ3bRzI";
    String apiKEY =  "AIzaSyDfQkB7ACKsFgCcbhnQ45hVG1E9Va8pT2k";


    String strNaziv;
    String strOpis;
    String strVlasnik;

    String strSlika;
    String strBarkod;

    JSONArray jsonArray;
    ListView listView;

    CustomAdapter customAdapter;

    ArrayList<String > listNaziv = new ArrayList<String>();
    ArrayList<String > listOpis = new ArrayList<String>();

    ArrayList<String > listSlika = new ArrayList<String>();
    ArrayList<String > listVlasnik = new ArrayList<String>();

    ArrayList<String > listBarkod = new ArrayList<String>();


    String scannedData;
    // globally
    TextView barcodeTextView;

    Button btn_list;
    Button btn_scan;

    Button signOutBtn;
    Button btn_add_item;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barcodeTextView = findViewById(R.id.text);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        signOutBtn = findViewById(R.id.signout);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });



        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(v ->{

           scanCode();
           // scan();

                });

        btn_list = findViewById(R.id.btn_list);
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listIntent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(listIntent);
            }
        });

        btn_add_item = findViewById(R.id.btn_add);
        btn_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MainActivity.this, AddItem.class);
                startActivity(addIntent);
            }
        });




            String urls = "https://sheets.googleapis.com/v4/spreadsheets/"+sheetID+"/values/Spisak?key="+apiKEY;

            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

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

    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                startActivity(new Intent(MainActivity.this,GRegister.class));
            }
        });
    }

    private void scan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a barcode");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }


    private void scanCode() {

        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);

    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->
    {
        int i  = 0;
        while( i < listBarkod.size()){
            if (result.getContents().equals(listBarkod.get(i))) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                intent.putExtra("barkod", listBarkod.get(i));
                intent.putExtra("naziv", listNaziv.get(i));
                intent.putExtra("opis", listOpis.get(i));
                intent.putExtra("vlasnik", listVlasnik.get(i));
                intent.putExtra("slika", listSlika.get(i));
                startActivity(intent);
                return;
        } else {
                i++;
            }


        } if ( i == listBarkod.size()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Podaci o uređaju:");
            builder.setMessage("Uređaj sa barkodom: " + result.getContents() + " nije unet") ;

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

            }



        if (result != null) {
            if (result.getContents() != null) {
                String barcode = result.getContents();
                barcodeTextView.setText(barcode);
            } else {
                barcodeTextView.setText("Scan canceled");
            }
        }

    });

}