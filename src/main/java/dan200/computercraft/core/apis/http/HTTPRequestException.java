/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.core.apis.http;

public class HTTPRequestException extends Exception
{
    private static final long serialVersionUID = 7591208619422744652L;

    public HTTPRequestException( String s )
    {
        super( s );
    }

    @Override
    public Throwable fillInStackTrace()
    {
        return this;
    }
}
