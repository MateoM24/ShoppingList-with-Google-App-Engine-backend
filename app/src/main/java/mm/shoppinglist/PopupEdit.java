package mm.shoppinglist;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;


import mateusz.mezyk.shoppinglist.backend.productApi.ProductApi;
import mateusz.mezyk.shoppinglist.backend.productApi.model.Product;

public class PopupEdit extends Activity{
    EditText edTV;
    Intent intent;
    LinearLayout llGroup;
    ProductApi ofyService;
    ArrayList<Long> idList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popupedit);
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width= displayMetrics.widthPixels;
        int hight=displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*.8),(hight));
        llGroup=(LinearLayout)findViewById(R.id.llGroup);
    }

    @Override
    protected void onResume() {
        super.onResume();
        intent=getIntent();
        idList=new ArrayList<>();
        EditText edTV;
        int count = intent.getIntExtra("count",-1);
        for (int i=1;i<=count;i++){
            edTV=new EditText(getBaseContext());
            String name=intent.getStringExtra(String.valueOf(i));
            Log.d("odbieram: ",name);
            Long id=intent.getLongExtra(String.valueOf(i)+"id",-1);
            idList.add(id);
            edTV.setText(name);
            llGroup.addView(edTV);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mapa=null;
    }

    public void saveChanges(View v){
        for(Long l:idList){
            new TaskRemoveOne(l).execute();
        }
        int count = llGroup.getChildCount();
        String newProduct;
        Product p;
        for (int i=0;i<count;i++){
            edTV=(EditText)llGroup.getChildAt(i);
            newProduct=edTV.getText().toString();
            p = new Product();
            p.setName(newProduct);
            new TaskAddOne(p).execute();
        }
        Toast.makeText(getBaseContext(),"Product(s) edited",Toast.LENGTH_SHORT).show();
        finish();
    }
    public void setOfyServ(){
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
            Log.d("stepy","TaskGetAll DoInBackground");
        }
    }
    private class TaskRemoveOne extends AsyncTask<Long,Void,Void> {

        Long id;

        public TaskRemoveOne(Long id){
            this.id=id;
        }

        @Override
        protected Void doInBackground(Long... params) {
            setOfyServ();
            try {
                ofyService.remove(id).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onResume();
        }
    }
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
