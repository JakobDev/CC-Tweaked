/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.util;

import java.io.Closeable;
import java.io.IOException;

public final class IoUtil
{
    private IoUtil() {}

    public static void closeQuietly( Closeable closeable )
    {
        try
        {
            closeable.close();
        }
        catch( IOException ignored )
        {
        }
    }
}
