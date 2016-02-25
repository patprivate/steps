package com.patfives.steps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.patfives.steps.model.DayView;
import com.patfives.steps.ui.ProgressWheel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.steps_list)
    RecyclerView recyclerView;

    @Bind(R.id.loading_spinner)
    ProgressWheel loadingSpinner;

    @Bind(R.id.empty_message)
    TextView emptyMessage;


    DataManager dataManager;
    StepsAdapter stepsAdapter;

    CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        compositeSubscription = new CompositeSubscription();
        dataManager = new DataManager(this);
        initList();
        loadSteps();
    }

    private void initList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        stepsAdapter = new StepsAdapter();
        recyclerView.setAdapter(stepsAdapter);
    }

    private void loadSteps(){
        loadingSpinner.setVisibility(View.VISIBLE);
        loadingSpinner.startSpinning();

        Subscription subscription = dataManager.loadSteps().subscribe(new Observer<List<DayView>>() {
            @Override
            public void onCompleted() {
                hideLoading();
                checkShowEmpty();
            }

            @Override
            public void onError(Throwable e) {
                hideLoading();
                checkShowEmpty();
                //we need to do more stuff to properly handle network errors but this will do for now
                Toast.makeText(MainActivity.this, "Unable to load fresh posts. Please try again later.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(List<DayView> o) {
                if(stepsAdapter != null) {
                    stepsAdapter.setItems(o);
                }
            }
        });
        compositeSubscription.add(subscription);
    }

    private void checkShowEmpty(){
        if(stepsAdapter != null && stepsAdapter.items != null){
            if(stepsAdapter.items.size() > 0){
                emptyMessage.setVisibility(View.GONE);
            }else{
                emptyMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideLoading(){
        loadingSpinner.stopSpinning();
        loadingSpinner.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        compositeSubscription.unsubscribe();
        super.onDestroy();
    }
}
