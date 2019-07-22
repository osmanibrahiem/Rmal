package com.rmal.task.Tools.Connection;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitTool {

    private static final String TAG = "RetrofitTool";

    //--------------------------------------------------------------------
    private static Retrofit retrofit = null;

    public Retrofit getRetrofit(String baseURL, Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient(context))
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getClient(Context context) {
//        File httpCacheDirectory = new File(context.getCacheDir(), "httpCache");
//        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
//                .cache(cache)
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public okhttp3.Response intercept(Chain chain) throws IOException {
//                        try {
//                            return chain.proceed(chain.request());
//                        } catch (Exception e) {
//                            Request offlineRequest = chain.request().newBuilder()
//                                    .header("Cache-Control", "public, only-if-cached," +
//                                            "max-stale=" + 60 * 60 * 24)
//                                    .build();
//                            return chain.proceed(offlineRequest);
//                        }
//                    }
//                })
                .addInterceptor(logging)
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
    }

}