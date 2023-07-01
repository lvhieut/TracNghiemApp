package com.example.tracnghiemapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tracnghiemapp.model.Category;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textViewHighScore;
    private Spinner spinner;
    private Button btnStart;
    private int highscore;
    int REQUEST_CODE_QUESTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Anhxa();
        loadHighScore();
        loadCategories();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuestion();
            }
        });
    }
    //load điểm cao nhất
    private void loadHighScore() {
        SharedPreferences preferences = getSharedPreferences("share",MODE_PRIVATE);
        highscore = preferences.getInt("highscore1",0);
        textViewHighScore.setText("Điểm cao : "+ highscore);
    }

    private void startQuestion() {
        Category category = (Category) spinner.getSelectedItem();
        int categoryID = category.getId();
        String categoryName = category.getName();

        Intent intent = new Intent(MainActivity.this,QuestionActivity_1.class);
        intent.putExtra("idcategories", categoryID);
        intent.putExtra("categoriesname", categoryName);
        // sử dụng hàm dưới đê có thể nhận lại dữ liệu kết quả trả về thông qua method activityResult()
       startActivityForResult(intent,REQUEST_CODE_QUESTION);
    }

    private void Anhxa(){
        textViewHighScore = (TextView) findViewById(R.id.txtScore);
        spinner = (Spinner) findViewById(R.id.spCategory);
        btnStart = (Button) findViewById(R.id.btnStart);
    }
    //load chu de
    private void loadCategories(){
        Database database = new Database(this);
        //get data
        List<Category> categories = database.getDataCategories();
        //tao adapter
        ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,categories);
        //hien thi
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //gan chu de le spinner
        spinner.setAdapter(categoryArrayAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_QUESTION){
            if(resultCode == RESULT_OK){
                int score = data.getIntExtra("score",0);

                if(score > highscore){
                    updateHighScore(score);
                }
            }
        }
    }

    private void updateHighScore(int score) {
        //gán điểm cao mới
        highscore = score;
        //set điểm cao bằng giá trị vừa gán
        textViewHighScore.setText("Điểm cao: " + highscore);
        //Lưu trữ điểm
        SharedPreferences preferences = getSharedPreferences("share",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        //gán giá trị điểm cao mới vào khoá
        editor.putInt("highscore1",highscore);
        editor.apply();
    }
}