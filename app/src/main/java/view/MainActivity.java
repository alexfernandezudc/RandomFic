package view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.brais.myapplication.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import model.Category;
import model.CategoryAdapter;
import model.OurAdapter;

public class MainActivity extends AppCompatActivity {

    private final static String PERSISTENCE_FILE = "categorydb";

    private ArrayList<Category> modelList;
    private ListView lista = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.modelList = new ArrayList<>();
        // Instanciamos la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Preparamos el adaptador
        final CategoryAdapter adapter = new CategoryAdapter(modelList, this);
        lista = (ListView) findViewById(R.id.listView);
        lista.setAdapter(adapter);
        lista.setClickable(true);
        lista.setLongClickable(true);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View view, final int pos, long id){
                Category selectedCategory = modelList.get(pos);
                // Almacenar datos que le pasamos a la otra actividad
                Intent i = new Intent(getApplicationContext(),CategoryActivity.class);
                i.putExtra("categoryName",selectedCategory.getName());
                i.putStringArrayListExtra("categoryItems",selectedCategory.getItems());
                i.putExtra("pos",pos);
                // Arrancar nueva actividad
                startActivityForResult(i,pos);
            }
        });

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int pos, long id) {
                // Instanciamos el dialogo y añadimos el manejador del botón aceptar
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View v = inflater.inflate(R.layout.input_dialog, null);
                builder.setView(v);
                builder.setTitle(R.string.dialog_title_edit_category);
                // Handler botón aceptar del diálogo.
                builder.setPositiveButton(R.string.dialog_positive_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        EditText editText = (EditText) v.findViewById(R.id.categoryInput);
                        Category c = modelList.get(pos);
                        c.setName(editText.getText().toString());
                        lista.invalidateViews();
                    }
                });
                // Handler botón cancelar del diálogo
                builder.setNegativeButton(R.string.dialog_negative_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        d.cancel();
                    }
                });
                builder.show();
                return true;
            }
        });
        cargarCategorias();
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
                Category newCategory = new Category("Nueva categoría");
                modelList.add(newCategory);
                lista.invalidateViews();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStop(){
        super.onStop();
        try {
            FileOutputStream outFile = openFileOutput(PERSISTENCE_FILE, Context.MODE_PRIVATE);
            OutputStreamWriter out = new OutputStreamWriter(outFile);
            for (Category c : modelList) {
                out.write(c.getName() + ",");
                for (String item : c.getItems())
                    out.write(item + ",");
                out.write("\n");
            }
            out.close();
            System.out.println("Datos guardados con éxito!");
        } catch (FileNotFoundException e){
            System.out.println(e + " | PERSISTENCE FILE NO ENCONTRADA");
        } catch (IOException e){
            System.out.println(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == requestCode){
            Category modifiedCategory = new Category(result.getStringExtra("categoryName"));
            modifiedCategory.setItems(result.getStringArrayListExtra("categoryItems"));
            modelList.set(resultCode, modifiedCategory);
            lista.invalidateViews();
        }
    }
    /**
     * Recupera los datos de la aplicación desde el fichero de persistencia.
    **/
    private void cargarCategorias(){
        try{
            InputStream inStream = openFileInput(PERSISTENCE_FILE);

            if (inStream != null){
                InputStreamReader inReader = new InputStreamReader(inStream);
                BufferedReader buffReader  = new BufferedReader(inReader);

                String line = "";

                while ((line = buffReader.readLine()) != null){
                    System.out.println(line);
                    String [] items = line.split(",");
                    Category newCategory = new Category(items[0]);
                    int i = 1;
                    for (i=1;items.length > i;i++)
                        newCategory.newItem(items[i]);
                    modelList.add(newCategory);
                }

                inStream.close();
            }
            lista.invalidateViews();

        }  catch (FileNotFoundException e){
            System.out.println(e + " | PERSISTENCE FILE NO ENCONTRADA");
        } catch (IOException e){
            System.out.println(e);
        }
    }

}
