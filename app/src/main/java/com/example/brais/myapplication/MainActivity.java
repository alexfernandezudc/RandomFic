package com.example.brais.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private static String PERSISTENCE_FILE = "categorydb";

    private ArrayList<String> modelList;
    private ListView lista = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    /**
        // Cargamos los datos
        if (savedInstanceState == null)
            this.modelList = new ArrayList<>();
        else
            this.modelList = savedInstanceState.getStringArrayList("lista");
**/
        this.modelList = new ArrayList<>();
        // Instanciamos la toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Preparamos el adaptador
        final OurAdapter adapter = new OurAdapter(modelList, this);
        lista = (ListView) findViewById(R.id.listView);
        lista.setAdapter(adapter);
        lista.setLongClickable(true);

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
                        modelList.set(pos,editText.getText().toString());
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
                modelList.add("Nueva categoría");
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
            for (String c : modelList)
                out.write(c+"\n");
            out.close();
            System.out.println("Datos guardados con éxito!");
        } catch (FileNotFoundException e){
            System.out.println(e + " | PERSISTENCE FILE NO ENCONTRADA");
        } catch (IOException e){
            System.out.println(e);
        }
    }

/**
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("lista", this.modelList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.modelList = savedInstanceState.getStringArrayList("lista");
    }
**/

    private void cargarCategorias(){
        try{
            InputStream inStream = openFileInput(PERSISTENCE_FILE);

            if (inStream != null){
                InputStreamReader inReader = new InputStreamReader(inStream);
                BufferedReader buffReader  = new BufferedReader(inReader);

                String newCategory = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((newCategory = buffReader.readLine()) != null)
                    modelList.add(newCategory);
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
