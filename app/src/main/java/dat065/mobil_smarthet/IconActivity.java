package dat065.mobil_smarthet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class IconActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button = (Button) findViewById(R.id.icon_button);

        Intent i = getIntent();
        if(i.hasExtra("icon")) {
            iconAction(i.getStringExtra("icon"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            //finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void iconAction(String icon) {
        button.setText(icon);
        getSupportActionBar().setTitle(icon);
        if(icon.equals("Temperature")) {
        } else if(icon.equals("Sound")) {
            button.setText(icon);
        } else if(icon.equals("Light")) {
            button.setText(icon);
        } else if(icon.equals("Accelerometer")) {
            button.setText(icon);
        } else if(icon.equals("Alarm")) {
            button.setText(icon);
        }
    }
}
