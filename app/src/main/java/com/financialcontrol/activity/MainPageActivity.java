package com.financialcontrol.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.financialcontrol.R;
import com.financialcontrol.data.AppData.AccountType;
import com.financialcontrol.dataObjects.PreferenceParams;
import com.financialcontrol.dataObjectsAdapters.ToolbarSpinnerAdapter;
import com.financialcontrol.utils.AppConsts;
import com.financialcontrol.utils.AppUtils;
import com.financialcontrol.utils.DBUtils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainPageActivity extends AppCompatActivity {

    View incomeDetails, expenseDetails;
    TextView filterTitle, incomeTV, expenseTV, incomeSumTV, expenseSumTV;
    RelativeLayout incomeRL, expenseRL;
    PreferenceParams settings;
    Toolbar toolbar;
    Drawer drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSpinner();
        //addDrawer();
        initialize();
    }

    private void setSpinner() {

        getSupportActionBar().setTitle("");
        String[] myResArray = getResources().getStringArray(R.array.sort_entry_by_choice);
        List<String> myResArrayList = Arrays.asList(myResArray);
        ToolbarSpinnerAdapter spinnerAdapter = new ToolbarSpinnerAdapter(this, myResArrayList);
        Spinner spinner = (Spinner) findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(spinnerAdapter);

    }

    private void addDrawer() {

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.main)
                                .withIcon(MaterialDesignIconic.Icon.gmi_info)
                )
                .build();
    }

    private void initialize() {
        //Initialize income view
        View incomeView = findViewById(R.id.mp_income);
        incomeTV = (TextView) incomeView.findViewById(R.id.tl_title);
        incomeTV.setText(getResources().getString(R.string.income));
        incomeTV.setTextColor(ContextCompat.getColor(this, R.color.green));
        incomeSumTV = (TextView) incomeView.findViewById(R.id.tl_sum);
        incomeDetails = incomeView.findViewById(R.id.main_card);
        incomeDetails.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AppUtils.ShowEntries(MainPageActivity.this, -1, -1, AccountType.INCOME, settings.getStartDate(), settings.getEndDate());
            }
        });

        //Initialize expense view
        View expenseView = findViewById(R.id.mp_expense);
        expenseTV = (TextView) expenseView.findViewById(R.id.tl_title);
        expenseTV.setText(getResources().getString(R.string.expense));
        expenseTV.setTextColor(ContextCompat.getColor(this, R.color.red));
        expenseSumTV = (TextView) expenseView.findViewById(R.id.tl_sum);
        expenseDetails = expenseView.findViewById(R.id.main_card);
        expenseDetails.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AppUtils.ShowEntries(MainPageActivity.this, -1, -1, AccountType.EXPENSE, settings.getStartDate(), settings.getEndDate());
            }
        });
        filterTitle = (TextView) findViewById(R.id.mp_title);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String sort_by = SP.getString("pref_sort_method", "30");
        settings = new PreferenceParams(sort_by);

        filterTitle.setText(settings.getRepresentedString_ToMainPage());

        Double sum1 = DBUtils.getAccountSum(this, AccountType.INCOME, -1, -1, AppConsts.EntryActivityMode.FILTER,
                settings.getStartDate(), settings.getEndDate());
        incomeSumTV.setText(String.valueOf(sum1));

        Double sum2 = DBUtils.getAccountSum(this, AccountType.EXPENSE, -1, -1, AppConsts.EntryActivityMode.FILTER,
                settings.getStartDate(), settings.getEndDate());
        expenseSumTV.setText(String.valueOf(sum2));

        PieChart pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(sum1.floatValue(), 0));
        entries.add(new Entry(sum2.floatValue(), 1));

        ArrayList<String> labels = new ArrayList();
        labels.add(getString(R.string.income));
        labels.add(getString(R.string.expense));

        int[] colors = new int[2];
        colors[0] = ContextCompat.getColor(this, R.color.green);
        colors[1] = ContextCompat.getColor(this, R.color.red);
        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(colors);
        PieData data = new PieData(labels, dataset);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setDescription("");
        pieChart.getLegend().setEnabled(false);
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.spin(500, 0, -360f, Easing.EasingOption.EaseInOutQuad);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        /*if (id == R.id.action_show_all) {
            AppUtils.ShowEntries(MainPageActivity.this, -1, -1, AccountType.ALL, settings.getStartDate(), settings.getEndDate());
			return true;
		}*/
        if (id == R.id.action_settings) {
            Intent showPrefs = new Intent(MainPageActivity.this, SettingsActivity.class);
            startActivity(showPrefs);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void AddEntryBtn(View view) {
        Intent AddNewEntry = new Intent(MainPageActivity.this, AddingEntryActivity.class);
        startActivity(AddNewEntry);
    }

    public void ShowPayMethodsBtn(View view) {
        Intent ShowPayMethods = new Intent(MainPageActivity.this, PaymentsActivity.class);
        startActivity(ShowPayMethods);
    }

    public void ShowAccountsBtn(View view) {
        Intent ShowAccounts = new Intent(MainPageActivity.this, AccountsTabActivity.class);
        ShowAccounts.putExtra("IsFromMainPage", true);
        startActivity(ShowAccounts);
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(getResources().getString(R.string.leaving_app_title));
            dialogBuilder.setMessage(getResources().getString(R.string.leaving_app_message));
            dialogBuilder.setPositiveButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainPageActivity.this, getResources().getString(R.string.goodbye), Toast.LENGTH_SHORT).show();
                    MainPageActivity.super.onBackPressed();
                }
            });
            dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }

}
