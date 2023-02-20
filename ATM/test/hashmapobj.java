package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class hashmapobj {

    public static void main(String[] args) {

        // Testing ArrayList of variable datatypes
        ArrayList<Object> list = new ArrayList<Object>();

        list.add(10);
        list.add(100L);
        list.add(10.2);
        list.add(10.3f);
        list.add("Hello");
        list.add('C');
        list.add(false);

        for(int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getClass().getName());
            System.out.println("i: " + list.get(i));
        }

        // Testing HashMap of variable datatypes
        HashMap<String, Object> hashmap = new HashMap<String, Object>();

        hashmap.put("a", 10);
        hashmap.put("b", 10l);
        hashmap.put("c", 10.2);
        hashmap.put("d", 10.2f);
        hashmap.put("e", true);
        hashmap.put("f", "hello");

        for(Map.Entry<String, Object> entry: hashmap.entrySet()) {
            System.out.println(entry.getValue().getClass().getName());
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }


    }

}