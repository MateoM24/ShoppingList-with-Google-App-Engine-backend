package mm.shoppinglist;

import android.app.Activity;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import mateusz.mezyk.shoppinglist.backend.productApi.ProductApi;
import mateusz.mezyk.shoppinglist.backend.productApi.model.Product;

public class Popup extends Activity{
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width= displayMetrics.widthPixels;
        int hight=displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(hight*0.4));
        editText=(EditText)findViewById(R.id.edit_tv);
    }
    public void add(View v){

        String newProduct=editText.getText().toString();
        String s=newProduct+" added";
        Product p=new Product();
        p.setName(newProduct);
        editText.setText("");
        new TaskAddOne(p).execute();
        Toast.makeText(getBaseContext(),s,Toast.LENGTH_SHORT).show();}

    class TaskAddOne extends AsyncTask<Product,Void,Void> {

        ProductApi ofyService;
        Product p;

        public TaskAddOne(Product p){
            this.p=p;
        }

        @Override
        protected Void doInBackground(Product... params) {
            if(ofyService==null){
                ProductApi.Builder builder=new ProductApi.Builder(AndroidHttp.newCompatibleTransport(),new AndroidJsonFactory(),null)
                        .setRootUrl("http://10.0.2.2:8081/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                                request.setDisableGZipContent(true);
                            }
                        });
                ofyService=builder.build();
            }
            try {
                ofyService.insert(p).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
