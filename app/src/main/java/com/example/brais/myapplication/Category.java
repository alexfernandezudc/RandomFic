package com.example.brais.myapplication;

import java.util.ArrayList;

/**
 * Created by brais on 26/10/16.
 */

public class Category extends ArrayList<String>{

    private String name = null;

    public Category(String name){
        this.name = name;
    }

    public String getName(){return this.name;}

    public void setName(String newName){this.name = newName;}

    @Override
    public String toString(){return this.name;}
}
