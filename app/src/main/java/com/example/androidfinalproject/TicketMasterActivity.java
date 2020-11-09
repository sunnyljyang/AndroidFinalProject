package com.example.androidfinalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TicketMasterActivity extends AppCompatActivity {

    private String city_name;
    private final static String API_KEY = "x6c7kkjtNtUoAyALeAun36evsYJYNbyJ";
//    private static int radius;
    private String URL;
    private SQLiteDatabase db;
    private List<TicketEvent> ticketEvents;
    private static final String ALL = "all";
    private static final String BY_NAME = "byName";
    private static final String DUPLICATE_CHECK = "dupCheck";
    private SharedPreferences pref;
    private ProgressBar progressBar;
    private TicketEventAdapter ticketEventAdapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_master);
        ticketEvents = new ArrayList<>();
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        loadFromDatabase(ALL, null, null);

        ticketEventAdapter = new TicketEventAdapter(ticketEvents);

        pref = getSharedPreferences("City", Context.MODE_PRIVATE);


        final Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        EditText input = findViewById(R.id.txtCity);
        input.setText(pref.getString("City", ""));
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnShowHistory = findViewById(R.id.btnShowHistory);
        ListView eventList = findViewById(R.id.EventList);
        eventList.setOnItemClickListener((parent, view, position, id)->{
            Intent intent = new Intent(TicketMasterActivity.this, TicketDetail.class);
            intent.putExtra("Name", ticketEvents.get(position).getName());
            intent.putExtra("StartDate", ticketEvents.get(position).getStart_date());
            intent.putExtra("Currency", ticketEvents.get(position).getCurrency());
            intent.putExtra("MinPrice", ticketEvents.get(position).getMinPrice());
            intent.putExtra("MaxPrice", ticketEvents.get(position).getMaxPrice());
            intent.putExtra("URL", ticketEvents.get(position).getUrl());
            intent.putExtra("ImageUrl", ticketEvents.get(position).getImageUrl());
            startActivity(intent);
        });
        btnSearch.setOnClickListener(v->{
            city_name = input.getText().toString();
            URL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey="+API_KEY+"&city="+city_name+"&radius=100";
            EventQuery event = new EventQuery();
            event.execute(URL);

        });
        btnShowHistory.setOnClickListener(v->{

        });
        eventList.setAdapter(ticketEventAdapter);
    }

    @Override
    protected void onPause() {

        super.onPause();
        pref.edit().putString("City", city_name).apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions,menu);
        MenuItem searchItem = menu.findItem(R.id.action_ticket_master);
        SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_ticket_master:
                startActivity(new Intent(TicketMasterActivity.this, TicketMasterActivity.class));
                break;
            case R.id.action_recipe_search:
                startActivity(new Intent(TicketMasterActivity.this, RecipeSearchActivity.class));
                break;
            case R.id.action_covid19_data:
                startActivity(new Intent(TicketMasterActivity.this, Covid19DataActivity.class));
                break;
            case R.id.action_audio_database:
                startActivity(new Intent(TicketMasterActivity.this, AudioDatabaseActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private boolean loadFromDatabase(String signal, String eId, String name){

        if(db==null) db = new DBOpener(this).getWritableDatabase();
        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {DBOpener.COLUMN_ID, DBOpener.COLUMN_EVENT_ID, DBOpener.COLUMN_NAME, DBOpener.COLUMN_START_DATE, DBOpener.COLUMN_CURRENCY, DBOpener.COLUMN_MIN_PRICE, DBOpener.COLUMN_MAX_PRICE, DBOpener.COLUMN_URL, DBOpener.COLUMN_IMAGE_URL};
        Cursor results = null;
        switch (signal){
            case ALL:
                results = db.query(false, DBOpener.TABLE_NAME, columns, null, null, null, null, null, null);
                break;
            case BY_NAME:
                results = db.query(false, DBOpener.TABLE_NAME, columns, DBOpener.COLUMN_NAME + "='" + name + "'", null, null, null, null, null);
                break;
            case DUPLICATE_CHECK:
                results = db.query(false, DBOpener.TABLE_NAME, columns, DBOpener.COLUMN_EVENT_ID+"='"+ eId + "'", null, null, null, null, null);
                return results.getCount()!=0;
        }


        //Now the results object has rows of results that match the query.
        //find the column indices:
        int eventIdColumnIndex = results.getColumnIndex(DBOpener.COLUMN_EVENT_ID);
        int nameColumnIndex = results.getColumnIndex(DBOpener.COLUMN_NAME);
        int startDateColumnIndex = results.getColumnIndex(DBOpener.COLUMN_START_DATE);
        int currencyColumnIndex = results.getColumnIndex(DBOpener.COLUMN_CURRENCY);
        int minPriceColumnIndex = results.getColumnIndex(DBOpener.COLUMN_MIN_PRICE);
        int maxPriceColumnIndex = results.getColumnIndex(DBOpener.COLUMN_MAX_PRICE);
        int urlColumnIndex = results.getColumnIndex(DBOpener.COLUMN_URL);
        int imgUrlColumnIndex = results.getColumnIndex(DBOpener.COLUMN_IMAGE_URL);
        int idColIndex = results.getColumnIndex(DBOpener.COLUMN_ID);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String eventId = results.getString(eventIdColumnIndex);
            String eName = results.getString(nameColumnIndex);
            String startDate = results.getString(startDateColumnIndex);
            String currency = results.getString(currencyColumnIndex);
            String minPrice = results.getString(minPriceColumnIndex);
            String maxPrice = results.getString(maxPriceColumnIndex);
            String url = results.getString(urlColumnIndex);
            String imgUrl = results.getString(imgUrlColumnIndex);
            long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            ticketEvents.add(new TicketEvent(id, eventId, eName, startDate, currency, minPrice, maxPrice, url, imgUrl));
        }
        return false;

    }

    private class TicketEventAdapter extends BaseAdapter{
        private List<TicketEvent> ticketEvents;
        private TicketEventAdapter(List ticketEvents){ this.ticketEvents = ticketEvents;}


        @Override
        public int getCount() {
            return ticketEvents.size();
        }

        @Override
        public Object getItem(int position) {
            return ticketEvents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ticketEvents.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = TicketMasterActivity.this.getLayoutInflater();
            View newView;
            newView = inflater.inflate(R.layout.event_list,parent, false);
            TextView eventLine = newView.findViewById(R.id.eventItem);
            TicketEvent ticketEvent = (TicketEvent) getItem(position);
            eventLine.setText(ticketEvent.getName());
            return newView;
        }
    }

    private static class DBOpener extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "DBTicketEvents";
        public static final String TABLE_NAME = "TicketEvents";
        private static final int VERSION_NUM = 1;
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_EVENT_ID = "EventId";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_START_DATE = "StartDate";
        public static final String COLUMN_CURRENCY = "Currency";
        public static final String COLUMN_MIN_PRICE = "MinimumPrice";
        public static final String COLUMN_MAX_PRICE = "MaximumPrice";
        public static final String COLUMN_URL = "URL";
        public static final String COLUMN_IMAGE_URL = "ImageURL";

        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_EVENT_ID + " text, " + COLUMN_NAME +  " text, " + COLUMN_START_DATE + " text, " + COLUMN_CURRENCY + " text, " + COLUMN_MIN_PRICE + " text, " + COLUMN_MAX_PRICE + " text, "+ COLUMN_URL + " text, " + COLUMN_IMAGE_URL + " text);";



        private DBOpener(@Nullable Context context) {
            super(context,DATABASE_NAME,null, VERSION_NUM );
        }

        public DBOpener(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
    private class EventQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String ret;
            String jsonStr = null;
            try {
                URL url = new URL(URL);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(100000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line=reader.readLine())!=null){
                    sb.append(line + "\n");
                }
                jsonStr = sb.toString();


            } catch (MalformedURLException e) {
                ret = "Malformed URL Exception";
            } catch (IOException e) {
                ret = "IO Exception";
            }
            JSONObject jsonObject = null;
            try {
                assert jsonStr != null;
                jsonObject = new JSONObject(jsonStr);
                JSONObject jsonObjEmbedded = jsonObject.getJSONObject("_embedded");
                JSONArray events = jsonObjEmbedded.getJSONArray("events");
                publishProgress(10);
                for(int i=0; i< events.length(); i++){
                    //TODO
                    String jId = ((JSONObject)events.get(i)).getString("id");
                    String jName = ((JSONObject)events.get(i)).getString("name");
                    String jUrl = ((JSONObject)events.get(i)).getString("url");
                    String jStartDate =((JSONObject)events.get(i)).getJSONObject("dates").getJSONObject("start").getString("localDate");
                    JSONObject jPriceRange = (JSONObject)(((JSONObject)events.get(i)).getJSONArray("priceRanges").get(0));
                    String jCurrency = jPriceRange.getString("currency");
                    String jMinPrice = jPriceRange.getString("min");
                    String jMaxPrice = jPriceRange.getString("max");
                    JSONObject jPromotionImg = (JSONObject)(((JSONObject)events.get(i)).getJSONArray("images").get(0));
                    String jPromotionImgUrl = jPromotionImg.getString("url");
                    if(!loadFromDatabase(DUPLICATE_CHECK, jId,null)){
                        ContentValues cv = new ContentValues();
                        cv.put(DBOpener.COLUMN_EVENT_ID, jId);
                        cv.put(DBOpener.COLUMN_NAME, jName);
                        cv.put(DBOpener.COLUMN_START_DATE, jStartDate);
                        cv.put(DBOpener.COLUMN_CURRENCY, jCurrency);
                        cv.put(DBOpener.COLUMN_MIN_PRICE, jMinPrice);
                        cv.put(DBOpener.COLUMN_MAX_PRICE, jMaxPrice);
                        cv.put(DBOpener.COLUMN_URL, jUrl);
                        cv.put(DBOpener.COLUMN_IMAGE_URL, jPromotionImgUrl);
                        long id = db.insert(DBOpener.TABLE_NAME, null, cv);
                        ticketEvents.add(new TicketEvent(id, jId, jName, jStartDate, jCurrency, jMinPrice, jMaxPrice, jUrl, jPromotionImgUrl));
                    }
                    publishProgress(10+(90 * i / (events.length()-1)));
                }
                ret = "DONE";
            } catch (JSONException e) {
                ret = "JSON Exception";
            }


            return ret;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            ticketEventAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
