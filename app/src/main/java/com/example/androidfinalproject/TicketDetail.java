package com.example.androidfinalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TicketDetail extends AppCompatActivity {
    private Intent intent;
    private ImageView promotionImage;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        TextView eventName = findViewById(R.id.event_name);
        TextView ticketDetail = findViewById(R.id.ticket_detail);
        promotionImage = findViewById(R.id.promotionImage);
        intent = getIntent();
        eventName.setText(intent.getStringExtra("Name"));
        ticketDetail.setText("Start Date: " + intent.getStringExtra("StartDate") + "\n"
                            + "Price Range: " + intent.getStringExtra("MinPrice") + " - " + intent.getStringExtra("MaxPrice") + " " + intent.getStringExtra("Currency") + "\n"
                            +"Website: " + intent.getStringExtra("URL"));

        new TicketDetailQuery().execute();


    }
    private class TicketDetailQuery extends AsyncTask<String, Integer, String>{
        private Bitmap image;
        @Override
        protected String doInBackground(String... strings) {
            String ret;
            URL imgUrl;
            try {
                imgUrl = new URL(intent.getStringExtra("ImageUrl"));
                HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(connection.getInputStream());
                }

                FileOutputStream fos = openFileOutput( "promotion.jpg", Context.MODE_PRIVATE);
                image.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();
                ret="DONE";
            } catch (MalformedURLException e) {
                ret = "Malformed URL Exception";
            } catch (FileNotFoundException e) {
                ret = "FileNotFound Exception";
            } catch (IOException e) {
                ret = "IO Exception";
            }

            return ret;
        }

        @Override
        protected void onPostExecute(String s) {
            promotionImage.setImageBitmap(image);
        }
    }
}