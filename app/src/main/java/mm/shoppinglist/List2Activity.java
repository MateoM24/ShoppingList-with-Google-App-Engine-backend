package mm.shoppinglist;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import java.io.IOException;
import java.util.List;

import mateusz.mezyk.shoppinglist.backend.productApi.ProductApi;
import mateusz.mezyk.shoppinglist.backend.productApi.model.Product;

public class List2Activity extends AppCompatActivity {

    List<Product> list;
    Intent intent;
    RadioGroup group;
    ProductApi ofyService;

    public List<Product> getList() {
        return list;
    }

    public void setList(List<Product> list) {
        this.list = list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);
        group=(RadioGroup)findViewById(R.id.radioGroup);
    }
    @Override
    protected  void onResume(){
        super.onResume();
        makeUpTheList();
         }

    protected void makeUpTheList(){
        new TaskGetAll().execute();
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu__shopping_list2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_option:
                intent=new Intent(this,Popup.class);
                startActivity(intent);
                return true;

            case R.id.delete_option:
                int selectedOnes=0;
                int countCTV=group.getChildCount();
                Log.d("group count: ",String.valueOf(countCTV));
                CheckBox ctv;
                for(int i=0;i<countCTV;i++){
                    ctv=(CheckBox)group.getChildAt(i);
                    if(ctv.isChecked()){
                        String s=ctv.getText().toString();
                        for (Product p:list){
                            if (p.getName().equals(s)||p.getName()==s){
                                new TaskRemoveOne(p.getId()).execute();
                                selectedOnes++;
                            }
                        }
                    }
                }
                Toast.makeText(getBaseContext(),"Usunięto "+selectedOnes+" produktów",Toast.LENGTH_SHORT).show();

                return true;

            case R.id.return_button:
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

    private class TaskGetAll extends AsyncTask<Void,Void,List<Product>>{

        public TaskGetAll() {
        }

        @Override
        protected List<Product> doInBackground(Void... params) {
           setOfyServ();
            try {
                return ofyService.list().execute().getItems();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            super.onPostExecute(products);
            Log.d("stepy", "TaskGetAll onPostExec, products==null?" + (products == null));
            list = products;
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

            if ((list != null)) {
            Product p;
            Log.d("size of list: ", String.valueOf(list.size()));
            radioGroup.removeAllViews();//novedad czy dziala?
            for (int i = 0; i < list.size(); i++) {
                p = list.get(i);
                Log.d("IDiki z bazy: ",String.valueOf(p.getId()));
                if (p.getDone())
                {
                    final CheckBox cb;
                    cb = new CheckBox(getBaseContext());
                    cb.setText(p.getName());
                    cb.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                    cb.setBackgroundColor(Color.GREEN);

                    radioGroup.addView(cb);
                    Log.d("ile razy loop",String.valueOf(i));
                }
            }
        }else Log.d("błąd", "zmiena 'list' jest null!");

        }
    }

    private class TaskRemoveOne extends AsyncTask<Long,Void,Void>{

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

    class TaskMarkUndone extends AsyncTask<Product,Void,Void> {

        Product p;

        public TaskMarkUndone(Product p){
            this.p=p;
        }

        @Override
        protected Void doInBackground(Product... params) {
           setOfyServ();
            p.setDone(false);
            try {
                ofyService.update(p.getId(),p).execute();
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
    public void markUndone(View v) {
        int selectedOnes = 0;
        int countCTV = group.getChildCount();
        Log.d("groupCountDoMarkDone): ", String.valueOf(countCTV));
        CheckBox ctv;
        for (int i = 0; i < countCTV; i++) {
            ctv = (CheckBox) group.getChildAt(i);
            if (ctv.isChecked()) {
                String s=ctv.getText().toString();
                for(Product p:list) {
                    if (p.getName().equals(s)) {
                        new TaskMarkUndone(p).execute();
                        selectedOnes++;
                    }
                }
            }
        }
        Toast.makeText(getBaseContext(), "Cofnięto " + selectedOnes + " produktów", Toast.LENGTH_SHORT).show();
    }
    public void goToAct1 (View v){
        Intent i=new Intent(this,List1Activity.class);
        startActivity(i);
    }
}
