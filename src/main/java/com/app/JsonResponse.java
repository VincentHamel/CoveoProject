package com.app;

import java.util.ArrayList;
/*
Simple class in order to marshallize an array of location in a Json response
 */
public class JsonResponse {

    private String message;
    private ArrayList<Location> locations;

    public JsonResponse(){
        message = "suggestions: ";
        locations = new ArrayList();
    }

    public void addLocation(Location loc){
        locations.add(loc);
    }

    public boolean isEmpty(){
        return locations.isEmpty();
    }
}
