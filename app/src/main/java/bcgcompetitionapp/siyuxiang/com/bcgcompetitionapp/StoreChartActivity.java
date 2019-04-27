package bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class StoreChartActivity extends AppCompatActivity {

    private LineChart lineChart;
    ArrayList<CaseData> storeNetIncomes = new ArrayList<CaseData>();
    List<Entry> entries = new ArrayList<Entry>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        Intent intent = getIntent();
        String storeAddress = intent.getStringExtra("storeAddress");
        lineChart = (LineChart) findViewById(R.id.storeLineChart);
        try {
            CaseDataSource ds = new CaseDataSource(this);
            ds.open();
            storeNetIncomes = ds.getSpecificStoreNetIncome(storeAddress);
            ds.close();
        }
        catch (Exception e) {
            e.getCause();
            Toast.makeText(StoreChartActivity.this, "Error retrieving store net incomes", Toast.LENGTH_LONG).show();
        }
        for (int i = 0; i < storeNetIncomes.size(); i++) {
            entries.add(new Entry(i+1, storeNetIncomes.get(i).getNetIncome()));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "Net Income");
        //String[] xData = new String[]{"Jan", "Feb", "Mar","Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

}
