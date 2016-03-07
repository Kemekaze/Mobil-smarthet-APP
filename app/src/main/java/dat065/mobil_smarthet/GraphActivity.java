package dat065.mobil_smarthet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import dat065.mobil_smarthet.constants.Sensors;
import dat065.mobil_smarthet.database.SensorDBHandler;
import dat065.mobil_smarthet.event.GraphEvent;
import dat065.mobil_smarthet.event.UpdateGUIEvent;
import dat065.mobil_smarthet.event.UpdateGraphEvent;
import dat065.mobil_smarthet.service.GraphService;

/**
 * Created by backevik on 16-02-14.
 */
public class GraphActivity extends AppCompatActivity {


    private Sensors sensor;
    private LineChart chart;
    private SensorDBHandler sensorDBHandler;
    private long lastData = 0;
    private int currentShowingGraph = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        startService(new Intent(this, GraphService.class));
        setContentView(R.layout.line_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        chart = (LineChart) findViewById(R.id.chart);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sensor = Sensors.match((int)extras.getSerializable("sensor"));
            LineData data = new LineData();
            chart.setData(data);
            chart.setDescription(sensor.getSymbol());
            EventBus.getDefault().postSticky(new UpdateGraphEvent(0, sensor));
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(sensor.getName());
        sensorDBHandler = new SensorDBHandler(this,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.line_chart_menu, menu);
        return true;
    }

    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, sensor.getName());
        set.setColor(ColorTemplate.rgb("#4CAF50"));
        set.setDrawFilled(true);
        set.setFillColor(ColorTemplate.rgb("#4CAF50"));
        set.setLineWidth(4);
        set.setDrawValues(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        return set;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateData(UpdateGUIEvent ev){
        if(ev.getType() == 1 && ev.getData() != null){
            long time = (long) ev.getData();
            Log.i("Graph","Updating Data Event");
            EventBus.getDefault().post(new UpdateGraphEvent(currentShowingGraph,sensor,time));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateData(GraphEvent ev){
        if (ev.getGraphType() != currentShowingGraph) {
            currentShowingGraph = ev.getGraphType();
            LineData data = new LineData();
            chart.setData(data);
        }
        LineData data = chart.getData();
        if (data != null) {
            ArrayList<Entry> dataEntries = ev.getData()[0];
            ArrayList<String> dataLabels = ev.getData()[1];
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }


            for (Entry s : dataEntries) {
                s.setXIndex(s.getXIndex() + set.getEntryCount());
                data.addEntry(s, 0);
            }
            for (String s : dataLabels)
                data.addXValue(s);
            chart.notifyDataSetChanged();
            chart.invalidate();
            chart.setVisibleXRangeMaximum(100);
            chart.moveViewToX(data.getXValCount() - 121);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        MenuItem itemTmp = null;
        if(id == R.id.line_chart_menu_hour){
            EventBus.getDefault().post(new UpdateGraphEvent(0,sensor));
            itemTmp=item;
        }else if(id == R.id.line_chart_menu_day) {
            EventBus.getDefault().post(new UpdateGraphEvent(1,sensor));
            itemTmp=item;
        }else if(id == R.id.line_chart_menu_week) {
            EventBus.getDefault().post(new UpdateGraphEvent(2,sensor));
            itemTmp=item;
        }else if(id == R.id.line_chart_menu_month) {
            EventBus.getDefault().post(new UpdateGraphEvent(3,sensor));
            itemTmp=item;
        }else if(id == R.id.line_chart_menu_reset) {
            Log.i("Chart","Reseting zoom");
            while(!chart.isFullyZoomedOut()){
                chart.zoomOut();
            }
        }else if(id == android.R.id.home) {
            finish();
        }
        if(itemTmp != null){
            if(item.isChecked())
                item.setChecked(false);
            else
                item.setChecked(true);
        }
        return super.onOptionsItemSelected(item);
    }
}