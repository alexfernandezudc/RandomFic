package model;

import java.util.ArrayList;

/**
 * author brais
 */

public class Category{

    private String name = null;
    private ArrayList<String> items;

    public Category(String name){
        this.name = name;
        this.items = new ArrayList<>();
    }

    public String getName(){return this.name;}

    public void setName(String newName){this.name = newName;}

    public ArrayList<String> getItems() { return this.items;}

    public void newItem(String newItem){
        items.add(newItem);
    }

    @Override
    public String toString(){return this.name;}
}
