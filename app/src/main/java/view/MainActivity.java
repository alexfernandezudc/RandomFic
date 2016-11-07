package view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.widget.Toast;

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

    // Random option names
    protected static CharSequence randomOptionNames[] =  new CharSequence[] {"Agitar", "Girar dos veces", "Colocar boca abajo"};
    protected static boolean aviableRandomOptions[] = new boolean[3];

    // Random option variables
    protected static int RANDOM_OPTION = 0;
    protected final static int SHAKE_OPTION = 0;
    protected final static int ROTATION_OPTION = 1;
    protected final static int UPSIDE_DOWN_OPTION = 2;


    private ArrayList<Category> modelList;
    private ListView lista = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Comprobar qué sensores hay disponibles --------------------------------------------------
        SensorManager sMan =  (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sen = null;
        int i = 0;
        if ((sen = sMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) != null)
            aviableRandomOptions[i++] = true;
        else
            aviableRandomOptions[i++] = false;
        if ((sen = sMan.getDefaultSensor(Sensor.TYPE_GYROSCOPE)) != null)
            aviableRandomOptions[i++] = true;
        else
            aviableRandomOptions[i++] = false;
        if ((sen = sMan.getDefaultSensor(Sensor.TYPE_PROXIMITY)) != null)
            aviableRandomOptions[i++] = true;
        else
            aviableRandomOptions[i++] = false;
        // ----------------------------------------------------------------------------------------
        this.modelList = new ArrayList<>();

        // Instanciamos la toolbar ----------------------------------------------------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Preparamos el adaptador ................................................................
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
            case R.id.action_random_settings:
                showRandomOptionsDialog();
                break;
            case R.id.action_new:
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
     * Muestra un diálogo que te permite elegir el método de selección aleatoria.
     */
    protected void showRandomOptionsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selección random");
        builder.setItems(randomOptionNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RANDOM_OPTION = which;
                if (aviableRandomOptions[which])
                    RANDOM_OPTION = which;
                else
                    Toast.makeText(MainActivity.this, "Tu smartphone no dispone del hardware necesario. Escoja otra.", Toast.LENGTH_LONG).show();
            }
        });
        builder.setCancelable(true);
        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
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

                String line;
                while ((line = buffReader.readLine()) != null){
                    System.out.println(line);
                    String [] items = line.split(",");
                    Category newCategory = new Category(items[0]);
                    int i;
                    for (i=1;items.length > i;i++)
                        newCategory.newItem(items[i]);
                    modelList.add(newCategory);
                }

                inStream.close();
            }
            lista.invalidateViews();

        }  catch (FileNotFoundException e){
            System.err.println(e + " | PERSISTENCE FILE NO ENCONTRADA");
        } catch (IOException e){
            System.err.println(e);
        }
    }

}
