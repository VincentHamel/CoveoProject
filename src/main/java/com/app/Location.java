package com.app;

public class Location {

    private int m_ID;
    private String m_name;
    private double m_latitude;
    private double m_longitude;
    private String m_countryCode;
    private String m_admin2Code;
    private double m_comparaisonScore ;

    public Location(int geonameID, String name, double latitude, double longitude, String countryCode, String admin2Code){
        m_ID = geonameID;
        m_name = name;
        m_latitude=latitude;
        m_longitude = longitude;
        m_countryCode = countryCode;
        m_admin2Code=admin2Code;
        m_comparaisonScore = 1;
    }

    public int getGeonameID(){
        return m_ID;
    }
    public String getName(){
        return m_name;
    }
    public double getLatitude(){
        return m_latitude;
    }
    public double getLongitude(){
        return m_longitude;
    }
    public String getCountryCode(){
        return m_countryCode;
    }
    public String getAdmin2Code(){
        return m_admin2Code;
    }

    public void setComparaisonScore(double x){
        m_comparaisonScore = x;

    }

    public double getComparaisonScore(){
        return m_comparaisonScore;
    }


}
