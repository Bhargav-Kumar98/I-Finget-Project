package com.example.ifingetproject.ui.portfolio;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ifingetproject.IFingetDatabaseHelper;
import com.example.ifingetproject.Stock;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PortfolioViewModel extends AndroidViewModel {
    private static final String API_KEY = "II8L91LIDRB6R7N9";
    private static final String BASE_URL = "https://www.alphavantage.co/query?";

    private final MutableLiveData<List<Stock>> stockList = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final OkHttpClient client = new OkHttpClient();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final IFingetDatabaseHelper databaseHelper;

    public PortfolioViewModel(Application application) {
        super(application);
        databaseHelper = new IFingetDatabaseHelper(application);
    }

    public LiveData<List<Stock>> getStockList() {
        return stockList;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadSavedStocks() {
        executor.execute(() -> {
            List<String> symbols = databaseHelper.getAllStockSymbols();
            List<Stock> stocks = new ArrayList<>();
            for (String symbol : symbols) {
                try {
                    Stock stock = fetchStockData(symbol);
                    if (stock != null) {
                        stocks.add(stock);
                    }
                } catch (IOException | JSONException e) {
                    error.postValue("Error fetching stock data for " + symbol + ": " + e.getMessage());
                }
            }
            stockList.postValue(stocks);
        });
    }

    public void addStock(String symbol) {
        executor.execute(() -> {
            databaseHelper.addStock(symbol);
            loadSavedStocks();
        });
    }

    private Stock fetchStockData(String symbol) throws IOException, JSONException {
        String url = BASE_URL + "function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + API_KEY;
        String response = fetchData(url);
        return parseStockDataResponse(response);
    }

    private String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }

    private Stock parseStockDataResponse(String response) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject globalQuote = jsonObject.getJSONObject("Global Quote");
        String symbol = globalQuote.getString("01. symbol");
        String price = globalQuote.getString("05. price");
        String change = globalQuote.getString("09. change");
        String changePercent = globalQuote.getString("10. change percent").replace("%", "");
        String volume = globalQuote.getString("06. volume");

        // Fetch company overview for additional data
        String companyOverviewUrl = BASE_URL + "function=OVERVIEW&symbol=" + symbol + "&apikey=" + API_KEY;
        String companyOverviewResponse = fetchData(companyOverviewUrl);
        JSONObject companyOverview = new JSONObject(companyOverviewResponse);
        String companyName = companyOverview.getString("Name");
        String marketCap = companyOverview.getString("MarketCapitalization");

        return new Stock(symbol, companyName, price, change, changePercent, volume, marketCap);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        ((java.util.concurrent.ExecutorService) executor).shutdown();
    }
}