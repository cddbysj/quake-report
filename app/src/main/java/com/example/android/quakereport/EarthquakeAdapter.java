package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by billwen on 2017/6/27.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    private static final String LOCATION_SEPARATOR = "of";

    public EarthquakeAdapter(@NonNull Context context, @NonNull List<Earthquake> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //获取当前位置相对应的数据源对象
        Earthquake currentEarthquake = getItem(position);

        //声明容器类
        ViewHolder vh;

        //判断当前是否有已经实例化的视图
        if (convertView == null) {
            //如果没有实例化的视图，则使用布局生成器inflate一个视图对象
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_item, parent, false);
            //创建容器类，用来缓存已经实例化的控件对象
            vh = new ViewHolder();
            vh.tvMagnitude = (TextView) convertView.findViewById(R.id.text_magnitude);
            vh.tvOffset = (TextView) convertView.findViewById(R.id.text_offset);
            vh.tvPrimaryLocation = (TextView) convertView.findViewById(R.id.text_location);
            vh.tvDate = (TextView) convertView.findViewById(R.id.text_date);
            vh.tvTime = (TextView) convertView.findViewById(R.id.text_time);
            //将包含控件对象的容器类，以object的形式传给convertView
            convertView.setTag(vh);
        } else {
            //从convertView获得之前缓存的ViewHolder对象
            vh = (ViewHolder) convertView.getTag();
        }
        //获取当前位置的Earthquake对象的各个属性，并设置到对应的控件上
        assert currentEarthquake != null;

        //获取震级数
        double magnitude = currentEarthquake.getMagnitude();
        //更新magnitude控件文本信息
        vh.tvMagnitude.setText(String.valueOf(magnitude));
        //根据震级设置不同的圆圈背景色
        //从TextView获取背景，该背景是一个GradientDrawable对象
        GradientDrawable magnitudeCircle = (GradientDrawable) vh.tvMagnitude.getBackground();
        //根据当前震级获取正确的背景色
        int magnitudeColor = getMagnitudeColor(magnitude);
        //设置圆圈的背景色
        magnitudeCircle.setColor(magnitudeColor);

        //更新location控件文本信息，包括位置偏移和主要位置两部分
        //先获取原始的位置信息
        String originLocation = currentEarthquake.getLocation();
        //位置偏移信息
        String primaryLocation;
        //主要位置信息
        String offset;
        //获取位置偏移信息和主要位置信息
        if (originLocation.contains(LOCATION_SEPARATOR)) {
            primaryLocation = originLocation.split(LOCATION_SEPARATOR, 2)[1];
            offset = originLocation.split(LOCATION_SEPARATOR, 2)[0] + LOCATION_SEPARATOR;
        } else {
            primaryLocation = originLocation;
            offset = getContext().getString(R.string.near_the);
        }
        //更新位置偏移控件文本信息
        vh.tvOffset.setText(offset);
        //更新主要位置控件文本信息
        vh.tvPrimaryLocation.setText(primaryLocation);

        //更新日期和时间控件信息
        //返回以毫秒计数的时间
        long time = currentEarthquake.getTime();
        //新建一个Date对象
        Date dateObject = new Date(time);
        //更新date控件文本信息
        String formattedDate = formatDate(dateObject);
        vh.tvDate.setText(formattedDate);
        //更新time控件文本信息
        String formattedTime = formatTime(dateObject);
        vh.tvTime.setText(formattedTime);

        //返回更新了控件内容的列表项视图
        return convertView;
    }

    /**
     * 返回震级对应的背景色
     *
     * @param magnitude 地震震级的双精度表示
     */
    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

    /**
     * 辅助函数，将Date对象转化为指定格式的日期字符串
     */
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        return dateFormat.format(date);
    }

    /**
     * 辅助函数，将Date对象转化为指定格式的时间字符串
     */
    private String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm, a", Locale.US);
        return timeFormat.format(date);
    }

    //一个容器类，用来缓存单个列表项视图中的各个控件
    //避免了多次使用findViewById()方法来实例化控件，提升性能
    private class ViewHolder {
        TextView tvMagnitude;
        TextView tvOffset;
        TextView tvPrimaryLocation;
        TextView tvDate;
        TextView tvTime;
    }
}
