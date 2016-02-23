package dat065.mobil_smarthet;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import dat065.mobil_smarthet.constants.Sensors;
import dat065.mobil_smarthet.database.SensorDBHandler;

/**
 * Created by backevik on 16-02-14.
 */
public class GraphActivity extends AppCompatActivity {

    private ArrayList<Entry> entries;
    private Sensors sensor;
    private LineChart chart;
    private SensorDBHandler sensorDBHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sensorDBHandler = new SensorDBHandler(this,null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sensor = Sensors.match((int)extras.getSerializable("sensor"));
            graph(sensorDBHandler.getWeeklyData(sensor));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void graph(HashMap<DateTime,Double> temp) {
        HashMap<DateTime,Double> tempMap = new HashMap<>(temp);
        ArrayList<DateTime> dailyData = new ArrayList<DateTime>(tempMap.keySet());
        entries = new ArrayList<>();
        Collections.sort(dailyData);
        int k = 0;
        ArrayList<String> labels = new ArrayList<String>();
        for(DateTime date : dailyData){
            entries.add(new Entry(tempMap.get(date).floatValue(),k));
            labels.add(date.monthOfYear().get()+"/"+date.dayOfMonth().get());
            k++;
        }

        LineDataSet dataset = new LineDataSet(entries, sensor.getName());
        dataset.setColor(ColorTemplate.getHoloBlue());
        dataset.setDrawFilled(true);
        dataset.setFillColor(ColorTemplate.getHoloBlue());
        dataset.setLineWidth(4);
        chart = (LineChart) findViewById(R.id.chart);
        chart.setDescription(sensor.getName());

        LineData data = new LineData(labels,dataset);
        chart.clear();
        chart.setData(data);

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
                final AlertDialog scopeDialog;
                CharSequence scope[] = new CharSequence[] {"Week", "Month"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(GraphActivity.this, R.style.AppCompatAlertScopeStyle);
                builder.setTitle("Pick timescope");
                builder.setItems(scope, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            graph(sensorDBHandler.getWeeklyData(sensor));
                        } else {
                            graph(sensorDBHandler.getMonthlyData(sensor));
                        }
                    }
                });
                scopeDialog = builder.create();
                scopeDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                scopeDialog.show();
            }
        });
    }
}
