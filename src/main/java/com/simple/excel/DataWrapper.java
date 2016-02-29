package com.simple.excel;

/**
 * Data wrapper which store class and string representation of the data
 * @param <T> class generic.
 */
public class DataWrapper<T>
{

    private Class<T> clazz;
    private String stringValue;

    public DataWrapper(Class clazz, String stringValue)
    {
        this.clazz = clazz;
        this.stringValue = stringValue.trim();
    }

    public Class getClazz()
    {
        return clazz;
    }

    public String getStringValue()
    {
        return stringValue;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }

        DataWrapper<?> that = (DataWrapper<?>) o;

        if(clazz != null ? !clazz.equals(that.clazz) : that.clazz != null)
        {
            return false;
        }
        return !(stringValue != null ? !stringValue.equals(that.stringValue) : that.stringValue != null);

    }

    @Override
    public int hashCode()
    {
        int result = clazz != null ? clazz.hashCode() : 0;
        result = 31 * result + (stringValue != null ? stringValue.hashCode() : 0);
        return result;
    }
}
