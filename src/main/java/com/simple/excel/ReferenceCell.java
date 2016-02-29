package com.simple.excel;

/**
 * Represents the reference cell type of term.
 */
public class ReferenceCell
{

    String value;

    public ReferenceCell(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
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

        ReferenceCell that = (ReferenceCell) o;

        return !(value != null ? !value.equals(that.value) : that.value != null);

    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }
}
