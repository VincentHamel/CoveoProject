package com.app;

import java.util.ArrayList;
import java.util.List;

public class SimpleScoringAlgorithm {
    public SimpleScoringAlgorithm(){

    }

    /*
    Compare part of a word with a location name.
    Return a score between 0 and 1 ( 1 = perfect match, 0 = horrible match)
    Then adjust that score based on the distance between locations (the further, the lower the score)
   */

    public static void setScore(List<Location> locations, String wordPart, double latitude, double longitude, double distanceUpperLimit){
        for(Location location : locations){
            double score;
            //get an initial score based on the distance between words
            score = (double) wordPart.length() / (double) location.getName().length();
            //modify score based on the distance using Haversine formula
            double earthRadius = 6371; //kilometers
            double latDistance = Math.toRadians(latitude - location.getLatitude());
            double lonDistance = Math.toRadians(longitude - location.getLongitude());
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(location.getLatitude()))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = earthRadius * c;

            //normalize by distance ; a distance of 0 have an amazing score, a distance over the upper limit is rejected
            score = score * (1.0- (distance/distanceUpperLimit));
            location.setComparaisonScore(score);
        }
    }
}
