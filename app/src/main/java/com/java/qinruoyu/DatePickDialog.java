package com.java.qinruoyu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.java.qinruoyu.activity.SearchActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickDialog implements DatePicker.OnDateChangedListener {
    /**
     * 日期选择
     */
    private DatePicker mStartDateChoose;
    private DatePicker mEndDateChoose;

    /**
     * 对话框
     */
    private AlertDialog mDialog;
    /**
     * 设置时间
     */
    private String mStartTime, mEndTime;
    /**
     * 初始化时间
     */
    private String mInitDateTime;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    /**
     * Activity对象
     */
    private final SearchActivity mActivity;

    public DatePickDialog(SearchActivity activity, String startTime, String endTime) {
        this.mActivity = activity;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
    }

    /**
     * 弹出日期时间选择框方法
     *
     * @param dateText 需要设置的日期时间选择框
     * @return
     */
    public AlertDialog datePickerDialog(final Button dateText) {
        LinearLayout mInflater = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.dialog_date_picker, null);
        mStartDateChoose = mInflater.findViewById(R.id.start_date_choose);
        mEndDateChoose = mInflater.findViewById(R.id.end_date_choose);
        // 初始化日期时间
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(mStartTime));
            mStartDateChoose.init(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            calendar.setTime(sdf.parse(mEndTime));
            mEndDateChoose.init(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 创建对话框
        mDialog = new AlertDialog.Builder(mActivity).setTitle("设置搜索时间范围")
                .setView(mInflater)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.setStartTime(mStartTime);
                        mActivity.setEndTime(mEndTime);
                        Log.i("datePickerDialog", "开始时间：" + mStartTime);
                        Log.i("datePickerDialog", "结束时间：" + mEndTime);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
        // 设置日期改变
        onDateChanged(null, 0, 0, 0);
        return mDialog;
    }

    /**
     * 日期监听器
     */
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(mStartDateChoose.getYear(), mStartDateChoose.getMonth(), mStartDateChoose.getDayOfMonth());

        mStartTime = sdf.format(calendar.getTime());
        calendar.set(mEndDateChoose.getYear(), mEndDateChoose.getMonth(), mEndDateChoose.getDayOfMonth());
        mEndTime = sdf.format(calendar.getTime());
    }
}
