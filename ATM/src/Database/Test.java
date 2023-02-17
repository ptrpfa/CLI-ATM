// Package declaration
package Database;

import java.util.*;

// Main controller class
public class Test {

    // Program entrypoint
    public static void main(String[] args) {

        // Create SampleUser object
        SampleUser user = new SampleUser();

        // Get values
        HashMap<String, Object> hashmap = user.getValues("businessUSER", "6R7Q8OgY4Ys81U8NMzH3bR");
        System.out.println(hashmap.size());
        System.out.println(hashmap);
        for(Map.Entry<String, Object> entry : hashmap.entrySet()) {
            System.out.println("Key: " + entry.getKey());
            System.out.println("Value: " + entry.getValue());
        }

    }

}