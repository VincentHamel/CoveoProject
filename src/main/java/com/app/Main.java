
package com.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.*;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@SpringBootApplication
public class Main {

  private static final String DB_PASSWORD = "password";

  //config default values
  private double m_DistanceUpperLimit = 6000; //kilometers
  private double m_MinimalScore = 0.4;

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  private ArrayList<Location> locations = new ArrayList();

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  public String index(Map<String, Object>model) {
      model.put("first", "suggestion:name:latitude:longitude");
      model.put("second", "db:password" );
      model.put("third", "config:distance:minValue" );
      return "index";
  }

  @RequestMapping("/voice")
  public String voiceActived(@RequestParam Map<String,String> requestParams, Map<String, Object>model){

        VoiceRecognitor vc = new VoiceRecognitor();

        model.put("voice",  vc.getWordFromVoice("Voice/Amos.wav"));
        return "voice";
  }

  @RequestMapping(value = "/config", method = RequestMethod.GET)
  public String setConfig(@RequestParam Map<String,String> requestParams, Map<String, Object>model){
        m_DistanceUpperLimit = Double.parseDouble(requestParams.get("DistanceUpperLimit"));
        m_MinimalScore = Double.parseDouble(requestParams.get("MinimalScore"));

        model.put("config", "config modified");
        return "config";
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

        if(!password.equals(DB_PASSWORD)){
            model.put("message","wrong password");
            return "error";
        }

        InputStream inputStream = null;
        ClassLoader classLoader = this.getClass().getClassLoader();
        inputStream= classLoader.getResourceAsStream("rawData/CountyCode/CountyCode.txt");

        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DROP TABLE CountyCode");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CountyCode (Country VARCHAR(2) ," +
                    "Admin VARCHAR(2)," +
                    "Name VARCHAR(40))");

            PreparedStatement prepstmt = connection.prepareStatement(
                    "INSERT INTO CountyCode VALUES (?,?,?)");

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = br.readLine())!= null){
                String[] args = line.split("\\t");

                String country = args[0].substring(0,2);
                String provinceCode = args[0].substring(3,5);
                String provinceName = args[1];

                prepstmt.setString(1,country);
                prepstmt.setString(2,provinceCode);
                prepstmt.setString(3,provinceName);

                prepstmt.executeUpdate();
        }
            closeSilently(inputStream);
            connection.close();
            model.put("records"," Database successfully initialized");
            return "db";
        } catch (Exception e) {
            model.put("message", "error: " + e.getMessage());
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
        SimpleScoringAlgorithm.setScore(possibleLocation, wordPart,latitude,longitude, m_DistanceUpperLimit);
       // sort possibleLocation according to the biggest score
        Collections.sort(possibleLocation, Comparator.comparing(Location::getComparaisonScore));
        Collections.reverse(possibleLocation);

        Gson gson = new Gson();
        JsonResponse jsonResponse = new JsonResponse();
        for( Location loc: possibleLocation){
            if(loc.getComparaisonScore()>m_MinimalScore){
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
        } finally{
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
  Method to avoid clustering code by try-catching the closing of an inputStream in a finally block.
  */
  private void closeSilently(InputStream is){
      try {
          is.close();
      } catch (IOException e) {
      }
  }


}
