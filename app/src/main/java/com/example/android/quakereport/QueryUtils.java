package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {
    /**
     * 此class对象的名字，用于调试的日志标签
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * 从USGS服务器获取地震数据，返回值是一组地震列表
     * 这是唯一对外部开放的方法，它将下面的几个方法各自实现的逻辑串起来，形成流程
     */
    public static List<Earthquake> fetchDataFromServer(String requestUrl) {
        URL url = create(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        return extractEarthquakesFromJson(jsonResponse);
    }

    /**
     * 从代表URL的字符串返回对应的URL对象
     */
    private static URL create(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error in creating url", e);
        }
        return url;
    }

    /**
     * 向url对应的web server发起网络请求，返回JSON response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        //如果url为null,则直接返回
        if (url == null) return jsonResponse;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            //获取连接
            urlConnection = (HttpURLConnection) url.openConnection();
            //设置网络请求方法
            urlConnection.setRequestMethod("GET");
            //设置连接的时间限制
            urlConnection.setConnectTimeout(15000);
            //设置读取数据的时间限制
            urlConnection.setReadTimeout(10000);
            //开始连接到服务器
            urlConnection.connect();
            //如果响应代码为200（即服务器响应了请求），开始读取数据
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //从连接处获取input stream
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results", e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (inputStream != null) inputStream.close();
        }
        return jsonResponse;
    }

    /**
     * 从InputStream读取数据，返回值类型为String
     */
    private static String readFromInputStream(InputStream inputStream) throws IOException {
        //新建一个StringBuilder来缓存读取的字符串
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            String line;
            //新建一个带有缓存功能的字符输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Earthquake> extractEarthquakesFromJson(String jsonResponse) {
        //If json response is empty or null, then return early
        if (TextUtils.isEmpty(jsonResponse)) return null;
        // Create an empty ArrayList that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.
            //以提供的JSON响应样本为参数，新建一个根部JSONObject
            JSONObject root = new JSONObject(jsonResponse);
            //获取键值为"features"的JSONArray
            JSONArray features = root.getJSONArray("features");
            //遍历JSONArray中的每个JSONObject
            for (int i = 0; i < features.length(); i++) {
                //获取当前下标的JSONObject
                JSONObject currentEarthquake = features.getJSONObject(i);
                //获取键值为"properties"的JSONObject
                JSONObject properties = currentEarthquake.getJSONObject("properties");
                //获取地震的震级
                double magnitude = properties.getDouble("mag");
                //获取地震发生的地点
                String location = properties.getString("place");
                //获取地震发生的时间
                long time = properties.getLong("time");
                //获取有关此地震的网站链接
                String url = properties.getString("url");

                //根据获取的震级，地点，时间，网站链接，新建一个Earthquake对象
                Earthquake earthquake = new Earthquake(magnitude, location, time, url);
                //将这个对象添加到数据源中
                earthquakes.add(earthquake);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        // Return the list of earthquakes
        return earthquakes;
    }
}