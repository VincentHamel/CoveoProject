
package com.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@SpringBootApplication
public class Main {

  private static final double DISTANCE_UPPER_LIMIT = 6000; //kilometers
  private static final String DB_PASSWORD = "password";
  private static final double MINIMAL_SCORE = 0.4;

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  private ArrayList<Location> locations = new ArrayList();

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

    @RequestMapping("/")
    String index() {
        return "index";
      }

    /*
    Initialize the database with the CountyCode, admin region and name of region for both CA and US.
    Originally, the Database was also meant to contain all the location, but the current version of Heroku have a limit of 10'000 rows
     */
    @RequestMapping(value = "/db", method = RequestMethod.GET)
    public String db(@RequestParam Map<String,String> requestParams, Map<String, Object>model) {
        // this is to mimic a protected access; ideally this function would be on a totally different, private, dyno...
        // but Heroku free version only allow one dyno.
        String password=requestParams.get("password");

        if(password.equals(DB_PASSWORD)){
            try (Connection connection = dataSource.getConnection()) {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("DROP TABLE CountyCode");
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CountyCode (Country VARCHAR(2) ," +
                        "Admin VARCHAR(2)," +
                        "Name VARCHAR(40))");

                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','01', 'Alberta' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','02', 'British Columbia' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','03', 'Manitoba' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','04', 'New Brunswick' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','05', 'NewfoundLand and Labrador' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','07', 'Nova Scotia' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','08', 'Ontario' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','09', 'Prince Edward Island' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','10', 'Quebec' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','11', 'Saskatchewan' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','12', 'Yukon' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','13', 'NW Territories' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('CA','14', 'Nunavut' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','AR', 'Arkansas' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','DC', 'Washington,D.C.' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','DE', 'Delaware' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','FL', 'Florida' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','GA', 'Georgia' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','KS', 'Kansas' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','LA', 'Louisiana' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','MD', 'Maryland' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','MO', 'Missouri' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','MS', 'Mississippi' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','NC', 'North Carolina' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','OK', 'Oklahoma' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','SC', 'South Carolina' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','TN', 'Tenesse' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','TX', 'Texas' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','WV', 'West Virginia' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','AL', 'Alabama' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','CT', 'Connecticut' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','IA', 'Iowa' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','IL', 'Illinois' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','IN', 'Indiana' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','ME', 'Maine' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','MI', 'Michigan' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','MN', 'Minnesota' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','NE', 'Nebraska' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','NH', 'New Hampshire' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','NJ', 'New Jersey' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','NY', 'New York' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','OH', 'Ohio' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','RI', 'Rhode Island' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','VT', 'Vermont' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','WI', 'Wisconsin' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','CA', 'California' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','CO', 'Colorado' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','NM', 'New Mexico' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','MV', 'Nevada' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','UT', 'Utah' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','AZ', 'Arizona' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','ID', 'Idaho' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','MT', 'Montana' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','ND', 'North Dakota' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','OR', 'Oregon' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','SD', 'South Dakota' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','WA', 'Washington' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','WY', 'Wyoming' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','HI', 'Hawaii' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','AK', 'Alaska' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','KY', 'Kentucky' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','MA', 'Massachusetts' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','PA', 'Pennsylvania' )");
                stmt.executeUpdate("INSERT INTO CountyCode VALUES ('US','VA', 'Virginia' )");

                model.put("records"," Database successfully initialized");
                return "db";
            } catch (Exception e) {
                model.put("message", e.getMessage());
                return "error";
            }
        }else {
            model.put("message","wrong password");
            return "error";
        }
    }


    @RequestMapping(value = "/suggestions", method = RequestMethod.GET)
    public String suggestions(@RequestParam Map<String,String> requestParams, Map<String, Object>model) throws Exception{
        String wordPart=requestParams.get("q");
        double latitude=Double.parseDouble(requestParams.get("latitude"));
        double longitude=Double.parseDouble(requestParams.get("longitude"));

        if(locations.isEmpty()){
            initializeLocation();
        }

        List<Location> possibleLocation = getPossibleLocation(wordPart);
        for(Location loc : possibleLocation){
            setLocationScore(wordPart,latitude,longitude,loc);
        }

       // sort possibleLocation according to the biggest score
        Collections.sort(possibleLocation, Comparator.comparing(Location::getComparaisonScore));
        Collections.reverse(possibleLocation);

        Gson gson = new Gson();
        JsonResponse jsonResponse = new JsonResponse();
        for( Location loc: possibleLocation){
            if(loc.getComparaisonScore()>MINIMAL_SCORE){
               jsonResponse.addLocation(loc);
            }
        }

        if(jsonResponse.isEmpty()){
            model.put("suggestions", "no relevant suggestions available");
            return "suggestions";
        }else{
            model.put("suggestions", gson.toJson(jsonResponse));
            return "suggestions";
        }
    }

    /*
    load up the raw data comming from Geodata.com, transform it into useable Location object.
    Only have to be done once per loading
     */
    private void initializeLocation() {
        locations = new ArrayList<Location>();
        addLocations("rawData/Locations/CA.txt");
        addLocations("rawData/Locations/US.txt");
    }

    private void addLocations(String filepath){

        InputStream inputStream = null;
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            inputStream = classLoader.getResourceAsStream(filepath);

            BufferedReader brr = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = brr.readLine())!= null){
                String[] args = line.split("\\t");
                if(args[7].contains("PPL")){ //check if the location is a city, or atleast a populated area
                    int geonameID = Integer.parseInt(args[0]);
                    String name = args[1];
                    // args[2] is the ASCII version of args[1]
                    // args[3] are the surname of the city
                    double latitude = Double.parseDouble(args[4]);
                    double longitude = Double.parseDouble(args[5]);
                    // args[6] is a feature class -> not used for this app
                    // args[7] is a feature code to differentiate cities and locations.
                    String countryCode = args[8];
                    // args[9] is an alternate country code -> not used for CA and US
                    String admin2Code = args[10];
                    // args 11 to 18 are not used for this app.

                    locations.add(new Location(geonameID,name, latitude, longitude, countryCode, admin2Code));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }finally{
            closeSilently(inputStream);
        }

    }

    /*
    Heroku's tutorial example of loading datasource
    */
  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

  /*
  Create a sublist containing all location whose name contain the name requested
   */
  private List<Location> getPossibleLocation(String wordPart){

      List<Location> sublist = new ArrayList();

      for(int i = 0; i < locations.size(); i++){
          if( locations.get(i).getName().startsWith(wordPart)){
              sublist.add(locations.get(i));
          }
      }

      return sublist;
  }
  /*
  Compare part of a word with a location name.
  Return a score between 0 and 1 ( 1 = perfect match, 0 = horrible match)
  Then adjust that score based on the distance between locations (the further, the lower the score)
   */
  private void setLocationScore(String wordPart,double latitude, double longitude, Location location){

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
      score = score * (1.0- (distance/DISTANCE_UPPER_LIMIT));
      location.setComparaisonScore(score);
  }

  /*
  Method to avoid clustering code by try-catching the closing of an inputStream in a finally block.
  */
  private void closeSilently(InputStream is){
      try {
          is.close();
      } catch (IOException e) {
      }
  }


}
