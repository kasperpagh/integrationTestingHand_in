package cphbusiness.pagh;

import java.awt.image.DataBuffer;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Database
{

    private String filename;

    public Database(String filename)
    {
        this.filename = filename;
    }
    /*
    Writes a given DataModel object to the DB, assuming no key-collosions and no numbers in the data
     */
    public boolean dbWriter(DataModel data) throws UnsupportedEncodingException
    {
        File file;
        file = new File(filename);
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (Main.indexMap != null && Main.indexMap.containsKey(data.getKey()))
        {
            return false;
        }
        if (data.getData().matches(".*\\d+.*"))
        {
            return false;
        }
        try (FileOutputStream fop = new FileOutputStream(file, true))
        {
            if (finder(Integer.parseInt(data.getKey())) == null)
            {
                String sData = data.toString();
                byte[] bytes = sData.getBytes("US-ASCII");
                StringBuilder binary = new StringBuilder();
                for (byte b : bytes)
                {
                    int val = b;
                    for (int i = 0; i < 8; i++)
                    {
                        binary.append((val & 128) == 0 ? 0 : 1);
                        val <<= 1;
                    }
                }
                fop.write(binary.toString().getBytes("US-ASCII"));
                fop.flush();
                fop.close();
            }
            else
            {
                return false;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        setIndexMap();
        return true;
    }

    /*
    Reads the entire DB and returns it as an ASCII string.
     */
    public String dbReader() throws UnsupportedEncodingException
    {
        File file;
        file = new File(filename);

        byte[] result = new byte[(int) file.length()];
        try
        {
            InputStream input = null;
            try
            {
                int totalBytesRead = 0;
                input = new BufferedInputStream(new FileInputStream(file));
                while (totalBytesRead < result.length)
                {
                    int bytesRemaining = result.length - totalBytesRead;
                    int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                    if (bytesRead > 0)
                    {
                        totalBytesRead = totalBytesRead + bytesRead;
                    }
                }
            }
            finally
            {
                input.close();
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        String res = new String(result, "US-ASCII");
        int splitSize = 8; //If another encoding is chosen, this can be changed to the number of bits pr. char in that encoding

        if (res.length() % splitSize == 0) //tjekker om antallet af bits, går op med 8 (altså 1 byte == 1 ascii char)
        {
            int index = 0;
            int position = 0;

            byte[] resultByteArray = new byte[res.length() / splitSize];
            StringBuilder text = new StringBuilder(res);

            while (index < text.length())
            {
                String binaryStringChunk = text.substring(index, Math.min(index + splitSize, text.length()));
                Integer byteAsInt = Integer.parseInt(binaryStringChunk, 2);
                resultByteArray[position] = byteAsInt.byteValue();
                index += splitSize;
                position++;
            }

            return new String(resultByteArray, "US-ASCII");
        }
        else
        {
            return "not mod 8"; // IE. The number of bits doesn't match up to any sensible ascii chars. Because 8 bit pr char.
        }

    }

    /*
    Finds a given record, without the use of an index
     */
    public String finder(int key) throws UnsupportedEncodingException
    {
        Pattern pattern = Pattern.compile("#" + key + ",(.*?)#");
        String toMatch = dbReader();
        Matcher m = pattern.matcher(toMatch);
        String retS = null;
        while (m.find())
        {
            retS = m.group(0);
        }

        if (retS == null || retS.equalsIgnoreCase(""))
        {
            return null;
        }
        else
        {
            return retS;
        }
    }

    /*
    Finds a given record, through the use of the index
     */
    public String findWithIndex(int key) throws UnsupportedEncodingException
    {
        String index = Main.indexMap.get("" + key);
        String[] bitBorders = index.split("-");

        try
        {
            return dbReader().substring(Integer.parseInt(bitBorders[0]) / 8, Integer.parseInt(bitBorders[1]) / 8);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return dbReader().substring(Integer.parseInt(bitBorders[0]) / 8);
        }
    }

    /*
    Sets the index, is called each time a new record is added.
     */
    public void setIndexMap() throws UnsupportedEncodingException
    {

        String db = dbReader();
        Main.indexMap = new HashMap<>();
        List<String> keys = new ArrayList<>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(db);
        while (m.find())
        {
            keys.add(m.group());
        }

        for (int i = 0; i < keys.size(); i++)
        {
            if (i == keys.size() - 1)
            {
                Main.indexMap.put(keys.get(i), ((db.indexOf(keys.get(i)) * 8) - 8) + "-");
            }
            else
            {
                Main.indexMap.put(keys.get(i), (db.indexOf(keys.get(i)) * 8) - 8 + "-" + ((db.indexOf(keys.get(i + 1)) * 8) - 8));
            }

        }
    }
}
