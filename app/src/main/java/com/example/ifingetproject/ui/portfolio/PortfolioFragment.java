/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject.ui.portfolio;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifingetproject.NewsAdapter;
import com.example.ifingetproject.NewsArticle;
import com.example.ifingetproject.PolygonApiClient;
import com.example.ifingetproject.R;
import com.example.ifingetproject.Stock;
import com.example.ifingetproject.StockAdapter;
import com.example.ifingetproject.databinding.FragmentPortfolioBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PortfolioFragment extends Fragment {
    private FragmentPortfolioBinding binding;
    private PortfolioViewModel portfolioViewModel;
    private AutoCompleteTextView autoCompleteTextViewStockSymbol;
    private Button buttonSearch;
    private RecyclerView recyclerViewStocks;
    private StockAdapter stockAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPortfolioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        portfolioViewModel = new ViewModelProvider(this).get(PortfolioViewModel.class);

        initViews();
        setupRecyclerView();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        autoCompleteTextViewStockSymbol = binding.autoCompleteTextViewStockSymbol;
        buttonSearch = binding.buttonSearch;
        recyclerViewStocks = binding.recyclerViewStocks;
    }

    private void setupRecyclerView() {
        stockAdapter = new StockAdapter(new ArrayList<>(), this::showStocksNewsDialog);
        recyclerViewStocks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewStocks.setAdapter(stockAdapter);
    }

    private void showStocksNewsDialog(Stock stock) {
        Context context = requireContext();
        final Dialog dialog = new Dialog(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.stock_news_dialog, null);

        dialog.setContentView(dialogView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView titleTextView = dialogView.findViewById(R.id.dialogTitle);
        titleTextView.setText(stock.getSymbol() + " News");

        RecyclerView stockNewsRecyclerView = dialogView.findViewById(R.id.stockNewsRecyclerView);
        stockNewsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Fetch news for the stock
        fetchNewsForStock(stock.getSymbol(), newsArticles -> {
            // Use requireActivity().runOnUiThread to update UI
            requireActivity().runOnUiThread(() -> {
                NewsAdapter newsAdapter = new NewsAdapter(newsArticles, article -> {
                    // Open the news article URL
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                    startActivity(intent);
                });
                stockNewsRecyclerView.setAdapter(newsAdapter);
            });
        });

        dialog.show();
    }

    private void fetchNewsForStock(String symbol, OnNewsReceivedListener listener) {
        String apiKey = "pDLyAKQxHdAvrdf5KN3Xgtl6br56N3FL";
        PolygonApiClient.fetchStockNews(symbol, apiKey, new PolygonApiClient.NewsCallback() {
            @Override
            public void onSuccess(List<NewsArticle> newsArticles) {
                // Sort news articles by date (most recent first)
                Collections.sort(newsArticles, (a1, a2) ->
                        a2.getPublishedDate().compareTo(a1.getPublishedDate()));
                listener.onNewsReceived(newsArticles);
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(requireContext(), "Error fetching news: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    interface OnNewsReceivedListener {
        void onNewsReceived(List<NewsArticle> newsArticles);
    }

    private void setupListeners() {
        buttonSearch.setOnClickListener(v -> searchStock());
    }

    private void observeViewModel() {
        portfolioViewModel.getStockList().observe(getViewLifecycleOwner(), stocks ->
                stockAdapter.updateStocks(stocks)
        );
        portfolioViewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        );
    }

    private void searchStock() {
        String symbol = autoCompleteTextViewStockSymbol.getText().toString().trim();
        if (symbol.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a stock symbol", Toast.LENGTH_SHORT).show();
            return;
        }
        portfolioViewModel.addStock(symbol);
    }

    @Override
    public void onResume() {
        super.onResume();
        portfolioViewModel.loadSavedStocks();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}