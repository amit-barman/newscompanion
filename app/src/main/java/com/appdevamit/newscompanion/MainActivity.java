package com.appdevamit.newscompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.CategoryClickInterface{

    private RecyclerView newsRV, categoryRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private ArrayList<CategoryModal> categoryModalArrayList;
    private CategoryAdapter categoryAdapter;
    private NewsAdapter newsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newsRV = findViewById(R.id.idNews);
        categoryRV = findViewById(R.id.idNewsCatagories);
        loadingPB = findViewById(R.id.pbLoading);
        articlesArrayList = new ArrayList<>();
        categoryModalArrayList = new ArrayList<>();
        newsAdapter = new NewsAdapter(articlesArrayList, this);
        categoryAdapter = new CategoryAdapter(categoryModalArrayList, this, this::onCategoryClick);
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(newsAdapter);
        categoryRV.setAdapter(categoryAdapter);
        getCategories();
        getNews("All");
        newsAdapter.notifyDataSetChanged();
    }

    // News Categories
    private void getCategories(){
        categoryModalArrayList.add(new CategoryModal("All", constants.IMG_ALL));
        categoryModalArrayList.add(new CategoryModal("Technology", constants.IMG_TECH));
        categoryModalArrayList.add(new CategoryModal("Science", constants.IMG_SCIENCE));
        categoryModalArrayList.add(new CategoryModal("Sports", constants.IMG_SPORT));
        categoryModalArrayList.add(new CategoryModal("General", constants.IMG_GENERAL));
        categoryModalArrayList.add(new CategoryModal("Business", constants.IMG_BUSINESS));
        categoryModalArrayList.add(new CategoryModal("Entertainment", constants.IMG_ENT));
        categoryModalArrayList.add(new CategoryModal("Health", constants.IMG_HEALTH));
        categoryAdapter.notifyDataSetChanged();
    }

    // Fetch News from api
    private void getNews(String category){
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        String url = constants.url + constants.API_KEY;
        String categoryUrl = constants.CategoryUrl + category + "&apiKey=" + constants.API_KEY;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<NewsModal> call;
        if(category.equals("All")){
            call = retrofitAPI.getAllNews(url);
        }else{
            call = retrofitAPI.getNewsByCategory(categoryUrl);
        }

        call.enqueue(new Callback<NewsModal>() {
            @Override
            public void onResponse(Call<NewsModal> call, Response<NewsModal> response) {
                NewsModal newsModal = response.body();
                loadingPB.setVisibility(View.GONE);
                ArrayList<Articles> articles = newsModal.getArticles();
                for(int i = 0; i < articles.size(); i++){
                    articlesArrayList.add(new Articles(articles.get(i).getTitle(), articles.get(i).getDescription(), articles.get(i).getUrlToImage(), articles.get(i).getUrl(), articles.get(i).getContent()));
                }
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Unable to get news", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onCategoryClick(int position) {
        String category = categoryModalArrayList.get(position).getCategory();
        getNews(category);
    }
}