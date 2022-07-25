package com.tids.shopncart.report;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.tids.shopncart.R;
import com.tids.shopncart.utils.BaseActivity;

public class ReportActivity extends BaseActivity {


    CardView cardSalesReport, cardGraphReport, cardExpenseReport, cardExpenseGraph;
    ImageView backBtn;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

//        getSupportActionBar().setHomeButtonEnabled(true); //for back button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
//        getSupportActionBar().setTitle(R.string.report);
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.menu_back);
        setSupportActionBar(toolbar);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        cardSalesReport = findViewById(R.id.card_sales_report);
        cardGraphReport = findViewById(R.id.card_graph_report);
        cardExpenseGraph = findViewById(R.id.card_expense_graph);
        cardExpenseReport = findViewById(R.id.card_expense_report);


        cardSalesReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, SalesReportActivity.class);
                startActivity(intent);
            }
        });


        cardGraphReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, GraphReportActivity.class);
                startActivity(intent);
            }
        });


        cardExpenseReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, ExpenseReportActivity.class);
                startActivity(intent);
            }
        });


        cardExpenseGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, ExpenseGraphActivity.class);
                startActivity(intent);
            }
        });
    }
}
