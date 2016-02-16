package dat065.mobil_smarthet;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by backevik on 16-02-14.
 */
public class GraphActivity extends AppCompatActivity {

    private ArrayList<Entry> entries;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        entries = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sensor = (Sensor) extras.getSerializable("sensor");
            Log.d("TEST", sensor.sensorData.toString());
        }
        graph();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void graph() {
        ArrayList<DateTime> dailyData = new ArrayList<DateTime>(sensor.getWeeklyData().keySet());
        Collections.sort(dailyData);
        int k = 0;
        ArrayList<String> labels = new ArrayList<String>();
        for(DateTime date : dailyData){
            entries.add(new Entry(sensor.getSensorData().get(date),k));
            labels.add(date.monthOfYear().get()+"/"+date.dayOfMonth().get());
            k++;
        }

        LineDataSet dataset = new LineDataSet(entries, sensor.type.name().toLowerCase() + ", weekly data");
        dataset.setColor(ColorTemplate.getHoloBlue());
        dataset.setDrawFilled(true);
        dataset.setFillColor(ColorTemplate.getHoloBlue());
        dataset.setLineWidth(6);
        final LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.setDescription(sensor.type.toString().toLowerCase());

        LineData data = new LineData(labels,dataset);
        chart.setData(data);

        findViewById(R.id.backChartButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.zoomOutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while(!chart.isFullyZoomedOut()){
                    chart.zoomOut();
                }

            }
        });

        findViewById(R.id.changeScopeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DateTime> dailyData = new ArrayList<DateTime>(sensor.getMonthlyData().keySet());
                Collections.sort(dailyData);
                int k = 0;
                ArrayList<String> labels = new ArrayList<String>();
                for(DateTime date : dailyData){
                    entries.add(new Entry(sensor.getSensorData().get(date),k));
                    labels.add(date.monthOfYear().get()+"/"+date.dayOfMonth().get());
                    k++;
                }

                LineDataSet dataset = new LineDataSet(entries, sensor.type.name().toLowerCase() + ", monthly data");
                dataset.setColor(ColorTemplate.getHoloBlue());
                dataset.setDrawFilled(true);
                dataset.setFillColor(ColorTemplate.getHoloBlue());
                dataset.setLineWidth(6);
                chart.setDescription(sensor.type.toString().toLowerCase());

                LineData data = new LineData(labels,dataset);
                chart.setData(data);
            }
        });
    }
}
