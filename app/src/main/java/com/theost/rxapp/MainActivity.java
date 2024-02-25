package com.theost.rxapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.theost.rxapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private EditText editText;
    private String text;
    private TextView textView;
    private final Scheduler.Worker worker = Schedulers.computation().createWorker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);

        binding.button.setOnClickListener(v -> {
            Log.i("INSIDE CLICK", "Click!");
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.button.setVisibility(View.GONE);
            text = editText.getText().toString().trim();

            Log.wtf("RXJAVA", "disposableMap -----");

            worker.schedule(this::Rx);
            }
        );
    }


    void Rx() {
        Disposable disposableMap = Api.getData()
                .map(values -> values
                        .stream()
                        .map(ApiObject::getValue)
                        .sorted((first, second) -> Integer.compare(-((first.length() - first.replace(text, "").length()) / text.length()), -((second.length() - second.replace(text, "").length()) / text.length())))
                        .limit(100)
                        .sorted()
                        .collect(Collectors.toList())
                )
                .subscribe(
                            res -> {
                                Log.wtf("RXJAVA", "Result: " + String.join("; ", res));
                                runOnUiThread(() -> {
                                    textView.setText(String.join("; ", res));
                                    binding.progressBar.setVisibility(View.GONE);
                                    binding.button.setVisibility(View.VISIBLE);
                                });},
                            throwable ->
                                    Log.wtf("RXJAVA", "onError: " + throwable),
                            () -> {
                                Log.wtf("RXJAVA", "onComplete");
                                worker.dispose();
                            }
                        );
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }}


