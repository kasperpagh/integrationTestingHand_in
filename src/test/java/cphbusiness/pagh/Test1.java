package cphbusiness.pagh;

import org.junit.*;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class Test1
{

    private static Database database;
    private static File databaseFile;
    private static List<DataModel> testData;


    @BeforeClass
    public static void setUp() throws FileNotFoundException, UnsupportedEncodingException
    {
        database = new Database("data2");
        databaseFile = new File("data2");

        testData = new ArrayList<>();

        File testDataFile = new File("testData");
        Scanner scanner = new Scanner(testDataFile);
        StringBuilder sb = new StringBuilder();
        try
        {
            scanner.useDelimiter(";");
            while (scanner.hasNext())
            {
//                sb.append(scanner.next()+", ");
                String[] strings = new String[2];
                strings = scanner.next().split(",");
                DataModel datamodel = new DataModel(strings[0] +"",strings[1]+"");testData.add(new DataModel(""+strings[0], strings[1]));
                testData.add(datamodel);
            }
            scanner.close();
        }
        finally
        {
            scanner.close();
        }


//        testData.add(new DataModel("1", "data point et") );
//        testData.add( new DataModel("2", "data point to"));
//        testData.add(new DataModel("3", "data point to") );
//        testData.add(new DataModel("4", "data point to") );
//        testData.add(new DataModel("5", "data point to") );
//        testData.add(new DataModel("6", "data point to") );
//        testData.add( new DataModel("1", "data point to") ); //won't save due to key-collision
//        testData.add(new DataModel("8", "123123123") ); //won't save due to numbers in the data

    }

    @Before
    public void before() throws IOException
    {
        database = new Database("data2");


        databaseFile.createNewFile();

    }

    @AfterClass
    public static void tearDown()
    {
        databaseFile.delete();

    }


    @Test //Uses the dbWriter method to create a new empty DB, then checks if it has been created properly
    public void test1() throws UnsupportedEncodingException
    {
        database.dbWriter(testData.get(0));
        assertTrue(databaseFile.exists());
    }


    @Test //Checks to see if the dbWriter method creates the index correctly
    public void test2() throws UnsupportedEncodingException
    {
        database.dbWriter(testData.get(0));
        assertThat(Main.indexMap.size(), is(equalTo(1)));

    }

    @Test //Checks to see if the dbWriter method creates the index correctly
    public void test4() throws UnsupportedEncodingException
    {
        database.dbWriter(testData.get(0));
        assertThat(Main.indexMap.get("1"), is(equalTo("0-128"))); //indicate that the data related to index 1, is located from byte 0 to the end byte.

    }


    @Test //Checks to see how the index handles multiple entries
    public void test3() throws UnsupportedEncodingException
    {
        for(DataModel dm : testData)
        {
            database.dbWriter(dm);
        }
        assertThat(Main.indexMap.toString().equalsIgnoreCase("{1=0-128, 2=128-256, 3=256-384, 4=384-512, 5=512-640, 6=640-}"), is(true));
    }

    @Test //Checks to see how the index handles multiple entries
    public void test5() throws UnsupportedEncodingException
    {
        for(DataModel dm : testData)
        {
            database.dbWriter(dm);
        }

        assertThat(Main.indexMap.size(), is(equalTo(6))); //six due to the fact that two of the dataModels are illegal and shouldn't be saved.
        /*
        I use the string representation of the database, rather than the individual entries by themselves. This is just for convenience
         */
    }

}
