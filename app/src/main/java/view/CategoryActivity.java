package view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.brais.myapplication.R;

import model.Category;
import model.OurAdapter;


/**
 * Created by Brais on 02/11/2016.
 */

public class CategoryActivity extends Fragment{

    private Category category = null;
    private ListView lista = null;
    // Atributos ligados al sensor de agitamiento
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Detector mDetector;
    // Atributos para onCreateView
    private LinearLayout ll;
    private FragmentActivity fa;

    private AlertDialog randomAlert = null;
    // How to do when sensor calls onRandomSelect()void
    private OnRandomSelectListener rslistener = new OnRandomSelectListener(){
        @Override
        public void onRandomSelect() {// Shaking Handler
            int randomPos = (int) (Math.random() * category.getItems().size());
            if (randomAlert == null || !randomAlert.isShowing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this.getActivity());
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
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Instanciar la categoría
        fa = super.getActivity();
        ll = (LinearLayout) inflater.inflate(R.layout.activity_main, container, false);
        i = fa.getIntent();

        category = new Category(i.getStringExtra("categoryName"));
        category.setItems(i.getStringArrayListExtra("categoryItems"));

        // Instanciamos la toolbar
        Toolbar toolbar = (Toolbar) ll.findViewById(R.id.toolbar);
        getActivity().setActionBar(toolbar);

        // Nombre de la actividad
        getActivity().setTitle("Categoría: " + category.getName());

        // Preparamos el adaptador de la lista ----------------------------------------------------
        final OurAdapter adapter = new OurAdapter(category.getItems(), this.getActivity());
        lista = (ListView) ll.findViewById(R.id.listView);
        lista.setAdapter(adapter);
        lista.setLongClickable(true);
        // Handler: pulsación larga en fila.
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int pos, long id) {
                // Instanciamos el dialogo y añadimos el manejador del botón aceptar
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this.getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
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

        // Preparamos los sensores para la selección aleatoria ------------------------------------
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        updateSensor();
        // Handler: activar el sensor de turno.
        mDetector.setOnRandomSelectListener(rslistener);

        // Preparamos el resultado por si el usuario no hace cambios.
        prepararResultados();

        return ll;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_random_settings:
                showRandomOptionsDialog();
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
        getActivity().setResult(getActivity().getIntent().getIntExtra("pos",0),resultado);
    }

    protected void showRandomOptionsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle("Selección random");
        builder.setItems(MainActivity.randomOptionNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (MainActivity.aviableRandomOptions[which]) {
                    MainActivity.RANDOM_OPTION = which;
                    updateSensor();
                } else
                    Toast.makeText(CategoryActivity.this.getActivity(),"Tu smartphone no dispone del hardware necesario. Escoja otra.",Toast.LENGTH_LONG).show();
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
     * Actualiza el sensor para la selección aleatoria en función del seleccionado actualmente.
     */
    private void updateSensor() {
        if (mDetector != null)
            mSensorManager.unregisterListener(mDetector);
        switch (MainActivity.RANDOM_OPTION) {
            case 0:
                if (MainActivity.aviableRandomOptions[0]) {
                    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    mDetector = new ShakeDetector2();
                    mSensorManager.registerListener(mDetector, mSensor,	SensorManager.SENSOR_DELAY_UI);
                    break;
                }
            case 1:
                if (MainActivity.aviableRandomOptions[1]) {
                    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    mDetector = new RotationDetector();
                    mSensorManager.registerListener(mDetector, mSensor,	SensorManager.SENSOR_DELAY_UI);
                    break;
                }
            case 2:
                if (MainActivity.aviableRandomOptions[2]) {
                    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                    mDetector = new UpsideDownDetector();
                    mSensorManager.registerListener(mDetector, mSensor,	SensorManager.SENSOR_DELAY_UI);
                    break;
                }
        }
        mDetector.setOnRandomSelectListener(rslistener);
    }

}
