package com.vacuum.app.metquiz.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vacuum.app.metquiz.Adapters.ProductAdapter;
import com.vacuum.app.metquiz.MainActivity;
import com.vacuum.app.metquiz.Model.Example;
import com.vacuum.app.metquiz.Model.Product;
import com.vacuum.app.metquiz.Model.QuestionModel;
import com.vacuum.app.metquiz.R;
import com.vacuum.app.metquiz.Utils.RegisterAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.vacuum.app.metquiz.MainActivity.TAG_QUESTIONS;
import static com.vacuum.app.metquiz.MainActivity.activityTitles;
import static com.vacuum.app.metquiz.MainActivity.navItemIndex;

/**
 * Created by Home on 11/28/2017.
 */

public class QuestionsFragment extends Fragment implements View.OnClickListener{

    WebView mWebView;
    String ROOT_URL = "http://192.168.1.6/";
    List<QuestionModel> questions ;
    TextView question_count,question,text_btn1,text_btn2,text_btn3,text_btn4,points,exam_name,total_score;
    LinearLayout buttonslayout;
    RelativeLayout result_layout,btn1_layout,btn2_layout,btn3_layout,btn4_layout;
    Button btn1,btn2,btn3,btn4,home;
    public static int x = 0;
    int timer_number,points_number,total_questions_dgreee;
    static String correct;
    Context mContext;
    static int degree = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.questions_fragment, container, false);

        mContext = this.getActivity();

        mWebView =  view.findViewById(R.id.webview);
        buttonslayout =  view.findViewById(R.id.buttonslayout);
        question_count =  view.findViewById(R.id.question_count);
        result_layout =  view.findViewById(R.id.result_layout);
        points =  view.findViewById(R.id.points);
        exam_name =  view.findViewById(R.id.exam_name);
        total_score =  view.findViewById(R.id.total_score);
        home =  view.findViewById(R.id.home);



        question =  view.findViewById(R.id.question);
        btn1 =  view.findViewById(R.id.btn1);
        btn2 =  view.findViewById(R.id.btn2);
        btn3 =  view.findViewById(R.id.btn3);
        btn4 =  view.findViewById(R.id.btn4);
        text_btn1 =  view.findViewById(R.id.text_btn1);
        text_btn2 =  view.findViewById(R.id.text_btn2);
        text_btn3 =  view.findViewById(R.id.text_btn3);
        text_btn4 =  view.findViewById(R.id.text_btn4);

        btn1_layout =  view.findViewById(R.id.btn1_layout);
        btn2_layout =  view.findViewById(R.id.btn2_layout);
        btn3_layout =  view.findViewById(R.id.btn3_layout);
        btn4_layout =  view.findViewById(R.id.btn4_layout);



        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);




        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //mWebView.clearCache(true);
        //mWebView.clearHistory();
        // mWebView.setWebChromeClient(new WebChromeClient());
        // mWebView.setWebViewClient(new WebViewClient());
        //mWebView.getSettings().setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        //mWebView.getSettings().setJavaScriptEnabled(true);
        //mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setBackgroundColor(0);

        mWebView.loadUrl("file:///android_asset/index.html");


        //====================================================================
        MainActivity.CURRENT_TAG =TAG_QUESTIONS;
        navItemIndex = 5;
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(activityTitles[5]);
//====================================================================

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
                popBackStack(getActivity().getSupportFragmentManager());
                //getActivity().getSupportFragmentManager().beginTransaction().remove(QuestionsFragment.this).commit();
            }
        });


        setup_questions();
        return view;
    }
    public static void popBackStack(FragmentManager manager){
        FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
        manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void setup_questions() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.getQuestions().enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {


                questions = new ArrayList<>();

                for (QuestionModel fruit : response.body().getQuestionModel()) {
                    questions.add(fruit);
                }
                points_number = response.body().getPoints();
                //points.setText(String.valueOf(response.body().getPoints()));
                timer_number = response.body().getTimer();
                exam_name.setText(response.body().getExam_name());
                total_questions_dgreee = questions.size() * points_number;
                quesion_setup();
                counter();

            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.e("TAG",t.toString());
            }
        });
    }

    private void quesion_setup() {
        buttonslayout.setVisibility(View.GONE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (x < questions.size()) {
                    buttonslayout.setVisibility(View.VISIBLE);
                    question_count.setText( (x+1) +  "/" + String.valueOf(questions.size()));
                    question.setText(questions.get(x).getQuestion());

                    if(questions.get(x).getAns1() == "")
                        btn1_layout.setVisibility(View.GONE);
                    if(questions.get(x).getAns2() == "")
                        btn2_layout.setVisibility(View.GONE);
                    if(questions.get(x).getAns3() == "" )
                        btn3_layout.setVisibility(View.GONE);
                    if(questions.get(x).getAns4() == "")
                        btn4_layout.setVisibility(View.GONE);

                        text_btn1.setText(questions.get(x).getAns1());
                        text_btn2.setText(questions.get(x).getAns2());
                        text_btn3.setText(questions.get(x).getAns3());
                        text_btn4.setText(questions.get(x).getAns4());

                    correct = questions.get(x).getCorrectAns();
                    Log.e("TAG :correct",correct);
                    x++;
                }else {
                    finish_answers();
                }
            }
        }, 500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
                if(text_btn1.getText().toString().equals(correct)){
                    degree  = degree + points_number;
                }
                quesion_setup();
                break;
            case R.id.btn2:
                if(text_btn2.getText().toString().equals(correct)){
                    degree  = degree + points_number;
                }
                quesion_setup();
                break;
            case R.id.btn3:
                if(text_btn3.getText().toString().equals(correct)){
                    degree  = degree + points_number;
                }
                quesion_setup();
                break;
            case R.id.btn4:
                if(text_btn4.getText().toString().equals(correct)){
                    degree  = degree + points_number;
                }
                quesion_setup();
              break;

        }
    }

    private void counter() {
        new CountDownTimer(timer_number*10000, 1000) {

            public void onTick(long millisUntilFinished) {
                points.setText(String.valueOf(millisUntilFinished / 1000));
            }
            public void onFinish() {
                finish_answers();
            }
        }.start();
    }

    private void finish_answers() {
        total_score.setText(total_questions_dgreee + "/"+degree);
        result_layout.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                retrieveProducts();
            }
        }).start();
    }


    List<Product> list ;
    private void retrieveProducts() {
        list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setName(getString(R.string.name_format, String.valueOf(i))+"WorldFuck");
            product.setImageUrl("https://picsum.photos/500/500?image" + i);
            product.setPrice(i == 0 ? 50 : i * 100);
            Log.e("TAG",product.getImageUrl().toString());
            list.add(product);
        }

        // insert product list into database
        MainActivity.get().getDB().productDao().insertAll(list);

        // disable flag for force update
        MainActivity.get().setForceUpdate(false);
    }

}