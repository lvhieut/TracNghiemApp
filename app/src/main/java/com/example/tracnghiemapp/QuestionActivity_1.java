package com.example.tracnghiemapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tracnghiemapp.model.Question;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class QuestionActivity_1 extends AppCompatActivity {

    private TextView getTvScore, getTvQuestion_Count, getTvTheme, getTvQuestion_View, getTvCount;
    private RadioButton rdo1, rdo2, rdo3, rdo4;
    private RadioGroup rdoGr;
    private Button btnXacNhan;

    private CountDownTimer countDownTimer;
    private ArrayList<Question> questionList;
    private long timeLeftInMillis;
    private int questionCounter;
    private int questionSize;

    private int score;
    private boolean answered;
    private Question currentQuestion;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question1);

        Anhxa();
        //Nhận dữ liệu từ bên MainActivity
        Intent intent = getIntent();
        int categoryID = intent.getIntExtra("idcategories",0);
        String categoryName = intent.getStringExtra("categoriesname");
        //hiển thị các textview
        getTvTheme.setText("Chủ đề : " + categoryName);

        Database database = new Database(this);
        //Danh sách list chứa câu hỏi
        questionList = database.getQuestion(categoryID);
        //Lấy kích cỡ danh sách là tổng số câu hỏi có trong database
        questionSize = questionList.size();
        //đảo vị trí các câu hỏi
        Collections.shuffle(questionList);
        //hiển thị câu hỏi
        showNextQuestion();
        startCountDown();

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!answered){
                    if(rdo1.isChecked() || rdo2.isChecked() || rdo3.isChecked() || rdo4.isChecked()){
                        checkAnswer();
                    }
                    else {
                        Toast.makeText(QuestionActivity_1.this,"Hãy chọn đáp án!!!",Toast.LENGTH_LONG).show();
                    }
                }
                    else {
                        showNextQuestion();
                    }
                }
        });
    }

    private void showNextQuestion() {
        rdo1.setTextColor(Color.BLACK);
        rdo2.setTextColor(Color.BLACK);
        rdo3.setTextColor(Color.BLACK);
        rdo4.setTextColor(Color.BLACK);

        rdoGr.clearCheck();
        //kiểm tra nếu còn câu hỏi thực hiện dòng lệnh bên dưới
        if(questionCounter < questionSize){
            //get data ở vị trí questionCounter
          currentQuestion = questionList.get(questionCounter);
          //hiển thị câu hỏi
          getTvQuestion_View.setText(currentQuestion.getQuestion());
          rdo1.setText(currentQuestion.getOption1());
          rdo2.setText(currentQuestion.getOption2());
          rdo3.setText(currentQuestion.getOption3());
          rdo4.setText(currentQuestion.getOption4());
          updateCountDownText();
          questionCounter++;
          //set câu hỏi tại vị trí hiện tại
          getTvQuestion_Count.setText("Câu hỏi : "+questionCounter +"/"+questionSize);
          //gán gtri false chưa trả lời
            answered = false;
          //thời gian chạy
            timeLeftInMillis = 30000;
        }
        else {
            finishQuestion();
        }
    }

    //method đếm ngược thời gian
    private void startCountDown(){
        countDownTimer = new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
            //cập nhật timer
            updateCountDownText();

            }

            @Override
            public void onFinish(){
             //hết giơ
            timeLeftInMillis = 0;

            updateCountDownText();
            //check đáp án đúng
            checkAnswer();
            }
        }.start();
    }

    private void checkAnswer() {
     answered = true;
     RadioButton rdoSelected = findViewById(rdoGr.getCheckedRadioButtonId());
     int answer = rdoGr.indexOfChild(rdoSelected) + 1;
     //neu la cau tra loi dung
     if(answer == currentQuestion.getAnswer()){
         score = score + 10;
         getTvScore.setText("Điểm : " + score);
     }

     showSolution();

    }

    private void showSolution() {
      rdo1.setTextColor(Color.argb(100,37,37,22));
      rdo2.setTextColor(Color.argb(100,37,37,37));
      rdo3.setTextColor(Color.argb(100,37,37,37));
      rdo4.setTextColor(Color.argb(100,37,37,37));

      switch (currentQuestion.getAnswer()){
          case 1:
              rdo1.setTextColor(Color.GREEN);
              getTvQuestion_View.setText("Đáp án là A");
              break;
          case 2:
              rdo2.setTextColor(Color.GREEN);
              getTvQuestion_View.setText("Đáp án là B");
              break;
          case 3:
              rdo3.setTextColor(Color.GREEN);
              getTvQuestion_View.setText("Đáp án là C");
              break;
          case 4:
              rdo4.setTextColor(Color.GREEN);
              getTvQuestion_View.setText("Đáp án là D");
              break;
      }
      if(questionCounter < questionSize){
          btnXacNhan.setText("Câu tiếp theo");
      }
      else{
          btnXacNhan.setText("Hoàn Thành");
      }
      countDownTimer.cancel();
    }

    private void updateCountDownText() {
     int minutes = (int) ((timeLeftInMillis/1000)/60);
     int seconds = (int) ((timeLeftInMillis/1000)%60);
     String timeFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);

     getTvCount.setText(timeFormatted);
     //dưới 10s thì báo đỏ
     if(timeLeftInMillis < 10000){
         getTvCount.setTextColor(Color.RED);
     }else {
         getTvCount.setTextColor(Color.argb(100,10,93,13));
     }
    }

    private void finishQuestion(){
        //chứa dữ liệu
        Intent intentResult = new Intent();
        intentResult.putExtra("score",score);
        setResult(RESULT_OK,intentResult);
        finish();
    }

    @Override
    public void onBackPressed() {
        count++;
        if(count >= 1){
            finishQuestion();
        }
        count = 0;

    }

    private void Anhxa(){
        getTvScore = (TextView) findViewById(R.id.tvScore);
        getTvQuestion_Count = (TextView) findViewById(R.id.tvQuestion_Count);
        getTvTheme = (TextView) findViewById(R.id.tvTheme);
        getTvQuestion_View = (TextView) findViewById(R.id.tv_ViewQuestion);
        getTvCount = (TextView) findViewById(R.id.tvCount);
        rdoGr = (RadioGroup) findViewById(R.id.radioGroup);
        rdo1 = (RadioButton) findViewById(R.id.rdoChoose1);
        rdo2 = (RadioButton) findViewById(R.id.rdoChoose2);
        rdo3 = (RadioButton) findViewById(R.id.rdoChoose3);
        rdo4 = (RadioButton) findViewById(R.id.rdoChoose4);
        btnXacNhan = (Button) findViewById(R.id.btnAcp);
    }
}