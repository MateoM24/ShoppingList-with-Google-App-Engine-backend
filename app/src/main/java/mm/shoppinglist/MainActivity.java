package mm.shoppinglist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;

//import mateusz.mezyk.shoppinglist.backend.FullList;

public class MainActivity extends AppCompatActivity {
    Button buttonSList;
    Button buttonOptions;
    String MyPrefs = "Prefs";
    String sizePrefs = "Size";
    SharedPreferences preferences;//=getSharedPreferences(MyPrefs, MODE_PRIVATE);
    //{if(preferences.getLong("tableID",0L)==0L){
            //SharedPreferences.Editor editor=preferences.edit();
            //editor.putLong("tableID",new FullList().getId());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSList = (Button) findViewById(R.id.ButtonToShoppigList);
        //final Intent intentToSList = new Intent(this, ProductListActivity.class);
        final Intent intentToSList = new Intent(this, List1Activity.class);
        buttonSList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentToSList);
            }
        });
        buttonOptions = (Button) findViewById(R.id.buttonToOptions);
        final Intent intentToOptions = new Intent(this, OptionsActivity.class);
        buttonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentToOptions);
            }
        });
    }

    public ArrayList<Button> getAllViews() {
        ArrayList<Button> views = new ArrayList<Button>();
        ViewGroup layout = (ViewGroup) findViewById(R.id.layout);
        int count = layout.getChildCount();
        for (int i = 0; i <= count; i++) {
            if (layout.getChildAt(i) instanceof Button) views.add((Button) layout.getChildAt(i));
        }
        return views;
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences = getSharedPreferences(MyPrefs, MODE_PRIVATE);
        if (preferences.contains(sizePrefs)) {
            ArrayList<Button> views = getAllViews();
            for (Button v : views)
                v.setTextSize(preferences.getInt(sizePrefs, 20));
        }
    }

}
