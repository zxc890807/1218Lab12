package com.example.lab12;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.xml.transform.Result;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_query).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                String url = "http://140.124.73.33:3001/station";

                Request req = new Request.Builder().url(url).build();

                new OkHttpClient().newCall(req).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
                    {
                        if (response.code() ==200){
                            if (response.body() == null)return;

                            Data data = new Gson().fromJson(response.body().string(),Data.class);

                            final String[] items = new String[data.result.results.length];

                            for (int i = 0;i<items.length;i++)
                                items[i] = "\n列車即將進入:"+data.result.results[i].Station+
                                        "\n列車行駛目的地"+data.result.results[i].Destination;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("台北捷運列車到站站名").setItems(items,null).show();
                                    }
                                });
                        }else if (!response.isSuccessful())
                            Log.e("伺服器錯誤",response.code()+" "+response.message());
                        else
                            Log.e("其他錯誤",response.code()+" "+response.message());
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e)
                    {
                        Log.e("查詢失敗",e.toString());
                    }
                });
            }
        });
    }

}

class Data{
    Result result;

    class Result{
        Results[] results;

        class Results{
            String Station;
            String Destination;
        }
    }
}