package MainTests;

import com.app.Location;
import com.app.SimpleScoringAlgorithm;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MainTest {

    private List<Location> locations = new ArrayList();
    @Before
    public void setUp() throws Exception{

        locations.add(new Location(123, "here", 50.00, 50.00, "CA", "01"));
        locations.add(new Location(1234, "saint-michel-des-saints", 30.00, 80.00, "CA", "01"));
    }

    @Test
    public void scoringAlgorithmTest()
    {
        SimpleScoringAlgorithm.setScore(locations, "here", 50.00, 50.00, 6000);
        assertEquals(1,locations.get(0).getComparaisonScore(), 0.05);
        assertEquals(0.07,locations.get(1).getComparaisonScore(), 0.05);
    }
}
