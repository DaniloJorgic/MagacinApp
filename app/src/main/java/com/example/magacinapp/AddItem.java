package com.example.magacinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 22;
    ImageView imageView;
    EditText editTextBarkod,editTextNaziv, editTextOpis, editTextVlasnik;
    Button buttonAddItem, btnpicture, btnBack ;
    Bitmap rbitmap;
    String userImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_item);

        editTextBarkod = (EditText)findViewById(R.id.et_item_name);
        editTextNaziv = (EditText)findViewById(R.id.et_brand);
        editTextOpis =(EditText)findViewById(R.id.et_opis);
        editTextVlasnik =(EditText)findViewById(R.id.et_vlasnik);

        buttonAddItem = (Button)findViewById(R.id.btn_add_item);
        buttonAddItem.setOnClickListener(this);

        btnpicture = findViewById(R.id.btncamera_id);
        imageView = findViewById(R.id.image);

        btnpicture.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(cameraIntent,REQUEST_CODE);



            }

        });
        btnBack = (Button) findViewById(R.id.btn_nazad);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddItem.this, MainActivity.class);
                startActivity(intent);;
            }
        });

    }

    //This is the part where data is transafeered from Your Android phone to Sheet by using HTTP Rest API calls

    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
        final String barkod = editTextBarkod.getText().toString().trim();
        final String naziv = editTextNaziv.getText().toString().trim();
        final String opis = editTextOpis.getText().toString().trim();
        final String vlasnik = editTextVlasnik.getText().toString().trim();




        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyx0iyljc3uLd7KRvkW09_L625dYKce8qbHvTWJOF-8Tk8fwLyX1WnOo2WlfxtIF2wPew/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(AddItem.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),AddItem.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                //here we pass params
                params.put("action","addItem");
                params.put("barkod",barkod);
                params.put("naziv",naziv);
                params.put("opis", opis);
                params.put("slika", userImage);
                params.put("vlasnik", vlasnik);

                return params;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }



    @Override
    public void onClick(View v) {

        if(v==buttonAddItem){
            addItemToSheet();

            //Define what to do when button is clicked
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            rbitmap = getResizedBitmap(photo,250);//Setting the Bitmap to ImageView
            userImage = getStringImage(rbitmap);
            imageView.setImageBitmap(rbitmap);

        } else{
            Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);

    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

}