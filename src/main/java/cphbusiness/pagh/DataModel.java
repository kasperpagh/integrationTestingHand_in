package cphbusiness.pagh;

import java.io.Serializable;

public class DataModel implements Serializable
{
    private String key;
    private String data;

    public DataModel(String key, String data)
    {
        this.key = key;
        this.data = data;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    @Override
    public String toString()
    {
        return "#" + key + "," + data;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }
}
