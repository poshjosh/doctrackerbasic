package com.doctracker.basic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Josh
 */
public class DoctrackerbasicTest {
    
    public DoctrackerbasicTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * Test of main method, of class Doctrackerbasic.
     */
    @Test
    public void testMain() {
        
        try{
            
//C:\Users\Josh\Google Drive\personal\BLW\First Timers and Evangelism            
            final Path inputPath = Paths.get(System.getProperty("user.home"), "Google Drive", "personal", "BLW", "First Timers and Evangelism", "contact_numbers.txt");
            final Path outputPath = Paths.get(inputPath.getParent().toString(), "contact_numbers_linear.txt");

            final Set<String> set = new HashSet(2000);
            
            try(BufferedReader reader = new BufferedReader(new FileReader(inputPath.toFile()))) {

                String line = null;
                while((line = reader.readLine()) != null) {
System.out.println("Adding: "+line);                
                    set.add(line);
                }
            }
            
            final StringBuilder builder = new StringBuilder((11 * set.size()) + 10);
            
            final Iterator<String> iter = set.iterator();
            while(iter.hasNext()) {
                
                builder.append(iter.next());
                
                if(iter.hasNext()) {
                    builder.append(',');
                }
            }
            
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {

                writer.write(builder.toString());
                
                writer.flush();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
