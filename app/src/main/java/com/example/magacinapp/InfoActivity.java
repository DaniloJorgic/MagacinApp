package com.example.magacinapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;


public class InfoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView barkodText = findViewById(R.id.barkod);
        TextView nazivText = findViewById(R.id.naziv);
        TextView opisText = findViewById(R.id.opis);
        TextView vlasnikText = findViewById(R.id.vlasnik);

        String strBarkod = (String) getIntent().getSerializableExtra("barkod");
        String strNaziv = (String) getIntent().getSerializableExtra("naziv");
        String strOpis = (String) getIntent().getSerializableExtra("opis");
        String strVlasnik = (String) getIntent().getSerializableExtra("vlasnik");
        String strSlika = (String) getIntent().getSerializableExtra("slika");

        barkodText.setText(strBarkod);
        nazivText.setText(strNaziv);
        opisText.setText(strOpis);
        vlasnikText.setText(strVlasnik);

        new DownloadImageFromInternet((ImageView) findViewById(R.id.slika)).execute(strSlika);

    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}