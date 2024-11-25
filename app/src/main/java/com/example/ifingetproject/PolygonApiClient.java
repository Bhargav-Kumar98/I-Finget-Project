package com.example.ifingetproject;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PolygonApiClient {
    private static final String BASE_URL = "https://api.polygon.io/v2/reference/news";
    private static final OkHttpClient client = new OkHttpClient();

    public interface NewsCallback {
        void onSuccess(List<NewsArticle> newsArticles);
        void onError(String errorMessage);
    }

    public static void fetchStockNews(String symbol, String apiKey, NewsCallback callback) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String endDate = sdf.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, -3);
        String startDate = sdf.format(calendar.getTime());

        String url = BASE_URL + "?ticker=" + symbol + "&published_utc.gte=" + startDate + "&published_utc.lte=" + endDate + "&apiKey=" + apiKey;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Unexpected response code: " + response);
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray results = jsonObject.getJSONArray("results");
                    List<NewsArticle> newsArticles = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject article = results.getJSONObject(i);
                        String title = article.getString("title");
                        String publishedDate = article.getString("published_utc");
                        String url = article.getString("article_url");
                        String imageUrl = article.optString("image_url", "");
                        String sentiment = article.optString("sentiment", "Neutral");
                        String sentimentReasoning = article.optString("sentiment_reasoning", "");

                        newsArticles.add(new NewsArticle(title, publishedDate, url, imageUrl, sentiment, sentimentReasoning));
                    }

                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(newsArticles));
                } catch (JSONException e) {
                    callback.onError("Error parsing JSON: " + e.getMessage());
                }
            }
        });
    }
}