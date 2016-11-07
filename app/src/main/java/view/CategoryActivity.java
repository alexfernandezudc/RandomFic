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

import com.example.brais.myapplication.R;

import model.Category;
import model.OurAdapter;

/**
 * Created by Brais on 02/11/2016.
 */

public class CategoryActivity extends AppCompatActivity {

    private Category category = null;
    private ListView lista = null;
    // Atributos ligados al sensor de agitamiento
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Detector mDetector;

    private AlertDialog randomAlert = null;

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

        // Nombre de la actividad
        setTitle("Categoría: " + category.getName());

        // Preparamos el adaptador
        final OurAdapter adapter = new OurAdapter(category.getItems(), this);
        lista = (ListView) findViewById(R.id.listView);
        lista.setAdapter(adapter);
        lista.setLongClickable(true);
        // Handler: pulsación larga en fila.
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int pos, long id) {
                // Instanciamos el dialogo y añadimos el manejador del botón aceptar
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View v = inflater.inflate(R.layout.input_dialog, null);
                builder.setView(v);
                builder.setTitle(R.string.dialog_title_edit_category);
                // Handler botón aceptar del diálogo.
                builder.setPositiveButton(R.string.dialog_positive_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        EditText editText = (EditText) v.findViewById(R.id.categoryInput);
                        category.getItems().set(pos,editText.getText().toString());
                        lista.invalidateViews();
                        prepararResultados();
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
        // Preparamos el detector de agitamientos
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Dependiendo del hardware elegiremos un sensor u otro
        if ((mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) != null)
            mDetector = new ShakeDetector2();
        else {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mDetector = new UpsideDownDetector();
        }
        // Handler: activar el sensor de turno.
        mDetector.setOnRandomSelectListener(new OnRandomSelectListener() {
            @Override
            public void onRandomSelect() {// Shaking Handler
                int randomPos = (int) (Math.random() * category.getItems().size());
                if (randomAlert == null || !randomAlert.isShowing()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                    builder.setTitle("¡Seleccionado aleatoriamente!");
                    builder.setMessage(category.getItems().get(randomPos));
                    builder.setCancelable(true);
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    randomAlert = builder.create();
                    randomAlert.show();
                }
            }
        });

        // Preparamos el resultado por si el usuario no hace cambios.
        prepararResultados();
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
                MainActivity.showRandomOptionsDialog(CategoryActivity.this);
                break;
            case R.id.action_new:
                String newItem = new String("Nuevo item");
                category.newItem(newItem);
                lista.invalidateViews();
                prepararResultados();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mDetector, mSensor,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mDetector);
        super.onPause();
    }

    /**
     * Guarda los cambios en el intent que almacena el resultado para la mainActivity
     */
    private void prepararResultados(){
        Intent resultado = new Intent();
        resultado.putExtra("categoryName",category.getName());
        resultado.putExtra("categoryItems",category.getItems());
        setResult(getIntent().getIntExtra("pos",0),resultado);
    }

}
