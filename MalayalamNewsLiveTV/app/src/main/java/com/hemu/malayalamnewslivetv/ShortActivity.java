package com.hemu.malayalamnewslivetv;

import static com.hemu.malayalamnewslivetv.WebActivity.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.hemu.malayalamnewslivetv.adopters.ShortsAdopters;
import com.hemu.malayalamnewslivetv.models.Common;
import com.hemu.malayalamnewslivetv.models.VerticalViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ShortActivity extends AppCompatActivity {
    ShortsAdopters shortsAdopters;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> desc = new ArrayList<>();
    ArrayList<String> image = new ArrayList<>();
    ArrayList<String> link = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short);
        getSupportActionBar().hide();

        SharedPreferences getShared = getSharedPreferences("shorts", MODE_PRIVATE);
        String JsonValue = getShared.getString("edit","noValue");
        final VerticalViewPager verticalViewPages = (VerticalViewPager) findViewById(R.id.VerticalViewPage);


        if (!JsonValue.equals("noValue")){
            shortsAdopters = new ShortsAdopters(ShortActivity.this,title,desc,image,link);
            verticalViewPages.setAdapter(shortsAdopters);
            try {
                JSONArray jsonArray = new JSONArray(JsonValue);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject channelData = jsonArray.getJSONObject(i);
                    title.add(channelData.getString("title"));
                    desc.add(channelData.getString("desc"));
                    image.add(channelData.getString("thumbnail"));
                    link.add(channelData.getString("link"));
                    shortsAdopters.notifyDataSetChanged();
                    Log.d(TAG, "1onErrorResponse: " + channelData.getString("desc"));
                }

            } catch (JSONException e) {
                Log.d(TAG, "1onErrorResponse: " + "channelData.getString()");
                throw new RuntimeException(e);
            }
        }else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("ലോഡ് ചെയ്യാൻ കുറച്ച് സമയമെടുക്കും");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "വീണ്ടും ശ്രമിക്കുക",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            shortsAdopters = new ShortsAdopters(ShortActivity.this,title,desc,image,link);
                            verticalViewPages.setAdapter(shortsAdopters);
                            try {
                                JSONArray jsonArray = new JSONArray(JsonValue);
                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject channelData = jsonArray.getJSONObject(i);
                                    title.add(channelData.getString("title"));
                                    desc.add(channelData.getString("desc"));
                                    image.add(channelData.getString("thumbnail"));
                                    link.add(channelData.getString("link"));
                                    shortsAdopters.notifyDataSetChanged();
                                    Log.d(TAG, "1onErrorResponse: " + channelData.getString("desc"));
                                }

                            } catch (JSONException e) {
                                Log.d(TAG, "1onErrorResponse: " + "channelData.getString()");
                                throw new RuntimeException(e);
                            }

                        }
                    });

            builder1.setNegativeButton(
                    "തിരികെ",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            onBackPressed();

                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        if (Common.isConnectToInternet(ShortActivity.this)) {
            new webScript().execute();
        }

    }

    private class webScript extends AsyncTask<Void , Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            SharedPreferences getShared = getSharedPreferences("shorts", MODE_PRIVATE);

            String JsonValue = getShared.getString("row","noValue");

            String JsonValueEdit = getShared.getString("edit","noValue");

            if (!JsonValue.equals("noValue") && !JsonValueEdit.equals("noValue")){
                try {
                    JSONArray jsonArray = new JSONArray(JsonValue);
                    JSONArray jsonArrayEdit = new JSONArray(JsonValueEdit);

                    JSONObject row = jsonArray.getJSONObject(0);
                    JSONObject edit = jsonArrayEdit.getJSONObject(0);

                    if (!row.getString("title").equals(edit.getString("title")) ||
                            edit.getString("desc").equals("")){
                        JSONArray arr = new JSONArray();
                        HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();
                        Log.d(TAG, "1onErrorResponse: " + "desc");
                        Document document  = null;
                        String desc;
                        String thumbnail;
                        for (int i = 0; i < 20; i++){
                            JSONObject channelData = jsonArray.getJSONObject(i);
                            document = Jsoup.connect(channelData.getString("link")).get();

                            Elements contents_img = document.select(".artcl_contents_img").select("img");
                            Elements img_container = document.select(".image-container").select("img");

                            if (!contents_img.isEmpty()){
                                Element imageSrc = contents_img.get(0);
                                if (imageSrc!=null){
                                    thumbnail = imageSrc.attr("src");
                                }else {
                                    thumbnail = "";
                                }
                            }else if (!img_container.isEmpty()){
                                Element imageSrc = img_container.get(0);
                                if (imageSrc!=null){
                                    thumbnail = imageSrc.attr("src");
                                }else {
                                    thumbnail = "";
                                }
                            }else {
                                thumbnail = "";
                            }

                            Elements rightSec = document.select(".khbr_rght_sec").select("p");
                            Elements container = document.select(".container").select("p");
                            Elements slider_con = document.select(".slider_con").select("p");
                            Elements all_p = document.select("p");
                            if (rightSec.first()!= null){
                                if (rightSec.text().length() > 400){
                                    desc = rightSec.text().substring(0,400);
                                }else {
                                    desc = rightSec.text();
                                }
                            }else if (container.first()!=null){
                                if (container.text().length() > 400){
                                    desc = container.text().substring(0,400);
                                }else {
                                    desc = container.text();
                                }
                            }else if (slider_con.first()!=null){
                                if (slider_con.text().length() > 401){
                                    desc = slider_con.text().substring(0,400);
                                }else {
                                    desc = slider_con.text();
                                }
                            }else if (all_p.first()!=null){
                                if (all_p.text().length() > 400){
                                    desc = all_p.text().substring(0,400);
                                }else {
                                    desc = all_p.text();
                                }
                            }else {
                                desc = "....";
                            }
                            JSONObject json = new JSONObject();

                            json.put("id",i);
                            json.put("title",channelData.getString("title"));
                            json.put("desc",desc);
                            json.put("link",channelData.getString("link"));
                            json.put("thumbnail",thumbnail);
                            map.put("json" + i, json);
                            arr.put(map.get("json" + i));
                            Log.d(TAG, "1onErrorResponse: " + desc);
                        }
                        SharedPreferences sharedPreferences = getSharedPreferences("shorts", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("edit",arr.toString());
                        editor.apply();
                    }

                } catch (JSONException e) {
                    //throw new RuntimeException(e);
                } catch (IOException e) {
                    // throw new RuntimeException(e);
                }

            }

            return null;
        }


    }



}