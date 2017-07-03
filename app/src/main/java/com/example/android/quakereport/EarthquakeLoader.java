package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by billwen on 2017/7/3.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {
    /**
     * 日志消息标签
     */
    private static final String LOG_TAG = EarthquakeLoader.class.getSimpleName();
    /**
     * 查询URL
     */
    private String mRequestUrl;

    /**
     * 构造新的异步任务
     *
     * @param context    活动的上下文
     * @param requestUrl 要查询数据的URL
     */
    public EarthquakeLoader(Context context, String requestUrl) {
        super(context);
        mRequestUrl = requestUrl;
    }

    @Override
    public List<Earthquake> loadInBackground() {
        if (mRequestUrl == null) {
            return null;
        }
        //执行网络请求，解析响应和提取地震列表
        List<Earthquake> earthquakes = QueryUtils.fetchDataFromServer(mRequestUrl);
        return earthquakes;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
