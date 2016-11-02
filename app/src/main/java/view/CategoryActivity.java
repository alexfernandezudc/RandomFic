package view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.brais.myapplication.R;

import model.Category;
import model.CategoryAdapter;
import model.OurAdapter;

/**
 * Created by Brais on 02/11/2016.
 */

public class CategoryActivity extends AppCompatActivity {

    private Category category = null;
    private ListView lista = null;

    @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Instanciar la categoría
        Intent i = getIntent();
        category = new Category(i.getStringExtra("categoryName"));
        category.setItems(i.getStringArrayListExtra("categoryItems"));

        // Instanciamos la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Preparamos el adaptador
        final OurAdapter adapter = new OurAdapter(category.getItems(), this);
        lista = (ListView) findViewById(R.id.listView);
        lista.setAdapter(adapter);
        lista.setLongClickable(true);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.opciones:
                return true;
            case R.id.action_nuevo:
                String newItem = new String("Nueva item");
                category.newItem(newItem);
                lista.invalidateViews();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
