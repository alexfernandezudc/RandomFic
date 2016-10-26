package com.example.brais.myapplication;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> modelList;
    ListView lista = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cargamos los datos
        if (savedInstanceState == null)
            this.modelList = new ArrayList<>();
        else
            this.modelList = savedInstanceState.getStringArrayList("lista");

        // Instanciamos la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Preparamos el adaptador
        final OurAdapter adapter = new OurAdapter(modelList, this);
        lista = (ListView) findViewById(R.id.listView);
        lista.setAdapter(adapter);
        lista.setLongClickable(true);

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0,View view, final int pos, long id){
                // Instanciamos el dialogo y añadimos el manejador del botón aceptar
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View v = inflater.inflate(R.layout.input_dialog,null);
                builder.setView(v);
                builder.setTitle("Añadir categoría");
                builder.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        EditText editText = (EditText) v.findViewById(R.id.categoryInput);
                        modelList.set(pos,editText.getText().toString());
                        lista.invalidateViews();
                    }
                });
                builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface d, int which){
                        d.cancel();
                    }
                });
                builder.show();

                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.opciones) {
            return true;
        } else if (id == R.id.action_nuevo) {
            modelList.add("Nueva categoría");
            lista.invalidateViews();
            System.out.println("esto es una pruebaaaa");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("lista",this.modelList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        this.modelList = savedInstanceState.getStringArrayList("lista");
    }

}
