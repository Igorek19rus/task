package test;

public class FormatErrorException extends RuntimeException
{
    public FormatErrorException()
    {
        super();
    }

    public FormatErrorException(final String s)
    {
        super(s);
    }

    public FormatErrorException(final String s, final Throwable throwable)
    {
        super(s, throwable);
    }

    public FormatErrorException(final Throwable throwable)
    {
        super(throwable);
    }
}
