package cphbusiness.pagh;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;


public class Main
{
    static HashMap<String, String> indexMap;

    public static void main(String[] args)
    {
        Database db = new Database("data");

        try
        {
            db.dbWriter(new DataModel("1", "jeg hedder KasperET")); //Saving data, Strings only
            db.dbWriter(new DataModel("2", "jeg hedder KasperTO"));
            db.dbWriter(new DataModel("3", "jeg hedder KasperTRE"));
            db.dbWriter(new DataModel("4", "jeg hedder KasperFIRE"));
            db.dbWriter(new DataModel("5", "jeg hedder KaspeFEMr"));
            db.dbWriter(new DataModel("5", "I'm not save due to key-collision")); //duplicate key no go
            db.dbWriter(new DataModel("52", "jeg hedder KasperToOGHalvtreds"));
            db.dbWriter(new DataModel("28", "I can't b3 sav3d b3cause i contain numb3rs in my data")); //A bit lazy i know, but RegEx is hard ^_^

            System.out.println("her er find with index: " + db.findWithIndex(1)); //I use the index to locate a specific key
            System.out.println("her er find with index: " + db.findWithIndex(3));
            System.out.println("her er find with index: " + db.findWithIndex(52));
            System.out.println("her er find with index: " + db.findWithIndex(1));

            System.out.println(db.dbReader()); // I print the entire DB
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.out.println(indexMap.toString());
        }


    }
}
