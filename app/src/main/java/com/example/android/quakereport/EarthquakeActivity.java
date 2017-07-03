/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {
    /**
     * 日志消息标签
     */
    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    /**
     * EarthquakeLoader ID的常量值。我们可使用任意整数。
     * 该设置仅当使用多个Loader时才起作用
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    /**
     * USGS 数据集中地震数据的 URL
     */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";

    //为实现更新适配器，使适配器变量成为全局变量
    private EarthquakeAdapter mAdapter;

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        //返回为指定的URL创建的一个新Loader
        return new EarthquakeLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {
        // 清除之前地震数据的适配器
        mAdapter.clear();

        // 如果存在 {@link Earthquake} 的有效列表，则将其添加到适配器的
        // 数据集。这将触发 ListView 执行更新。
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        //重置Loader 以便能够清除现有数据
        mAdapter.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link EarthquakeAdapter} of earthquakes
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        //为地震列表项设置事件监听器
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //找到目前正在被点击的列表项的地震对象
                Earthquake earthquake = mAdapter.getItem(i);
                //获取该地震对象相关的网站链接
                assert earthquake != null;
                String url = earthquake.getUrl();
                //新建一个Intent以查看此地震的相关网站
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(websiteIntent);
            }
        });

        //引用LoaderManager，以便能与Loader实现交互
        LoaderManager loaderManager = getLoaderManager();
        //初始化Loader
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
    }
}
