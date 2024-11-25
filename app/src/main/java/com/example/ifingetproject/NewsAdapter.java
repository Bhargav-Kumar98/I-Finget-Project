package com.example.ifingetproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsArticle> newsArticles;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NewsArticle article);
    }

    public NewsAdapter(List<NewsArticle> newsArticles, OnItemClickListener listener) {
        this.newsArticles = newsArticles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsArticle article = newsArticles.get(position);
        holder.bind(article, listener);
    }

    @Override
    public int getItemCount() {
        return newsArticles.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewNews;
        private TextView textViewTitle;
        private TextView textViewDate;
        private ImageView imageViewSentiment;
        private TextView textViewSentiment;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewNews = itemView.findViewById(R.id.imageViewNews);
            textViewTitle = itemView.findViewById(R.id.textViewNewsTitle);
            textViewDate = itemView.findViewById(R.id.textViewNewsDate);
            imageViewSentiment = itemView.findViewById(R.id.imageViewSentiment);
            textViewSentiment = itemView.findViewById(R.id.textViewSentiment);
        }

        public void bind(final NewsArticle article, final OnItemClickListener listener) {
            textViewTitle.setText(article.getTitle());
            textViewDate.setText(article.getFormattedDate());

            // Load image using Picasso
            Picasso.get()
                    .load(article.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageViewNews);

            // Set sentiment icon and text
            setSentimentIcon(article.getSentiment());
            textViewSentiment.setText(article.getSentiment());

            itemView.setOnClickListener(v -> listener.onItemClick(article));
        }

        private void setSentimentIcon(String sentiment) {
            int iconResId;
            switch (sentiment.toLowerCase()) {
                case "positive":
                    iconResId = R.drawable.smile;
                    break;
                case "negative":
                    iconResId = R.drawable.unhappy;
                    break;
                default:
                    iconResId = R.drawable.neutral;
            }
            imageViewSentiment.setImageResource(iconResId);
        }
    }
}