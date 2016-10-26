package com.example.brais.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Created by Brais on 25/10/2016.
 */

public class InputCategoryDialog extends DialogFragment {

    private String newCategoryName;

    public String getNewCategoryName(){return newCategoryName;}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.input_dialog,null);
        builder.setView(v);
        builder.setMessage("Editar categoría");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                TextView textView = (TextView) getActivity().findViewById(R.id.list_item_string);
                EditText editText = (EditText) v.findViewById(R.id.categoryInput);
                newCategoryName = editText.getText().toString();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                InputCategoryDialog.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

}
