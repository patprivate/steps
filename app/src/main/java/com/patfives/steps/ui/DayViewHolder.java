package com.patfives.steps.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.patfives.steps.R;
import com.patfives.steps.model.DayView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DayViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.date_header)
    TextView dateHeader;
    @Bind(R.id.average_steps)
    TextView averageSteps;
    @Bind(R.id.total_steps)
    TextView totalSteps;
    @Bind(R.id.total_progress)
    ProgressWheel totalProgress;

    Context context;

    public DayViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public void bind(DayView dayView){
        dateHeader.setText(dayView.dateHeader);

        if(dayView.totalSteps > 0) {
            double dailyPercentage = dayView.dailyPercentage;
            if(dailyPercentage > 1){
                dailyPercentage = 1;
            }
            totalProgress.incrementProgress((int) (dailyPercentage * 360));
        }else{
            totalProgress.resetCount();
        }

        int totalStrLength = String.valueOf(dayView.totalSteps).length();
        String totalStepsStr = context.getString(R.string.total_steps, dayView.totalSteps);

        Spannable totalStepsSpan = new SpannableString(totalStepsStr);
        totalStepsSpan.setSpan(new RelativeSizeSpan(0.4f), totalStrLength, totalStepsStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        totalSteps.setText(totalStepsSpan);


        int avgStrLength = String.valueOf(dayView.averageSteps).length();
        String averageStepsStr = context.getString(R.string.average_steps, dayView.averageSteps);

        Spannable averageStepsSpan = new SpannableString(averageStepsStr);
        averageStepsSpan.setSpan(new RelativeSizeSpan(0.4f), avgStrLength, averageStepsStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        averageSteps.setText(averageStepsSpan);

    }
}
