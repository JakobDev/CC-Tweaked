/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.core.computer;

import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.filesystem.IWritableMount;

import java.io.InputStream;

public interface IComputerEnvironment
{
    int getDay();

    double getTimeOfDay();

    boolean isColour();

    long getComputerSpaceLimit();

    String getHostString();

    int assignNewID();

    IWritableMount createSaveDirMount( String subPath, long capacity );

    IMount createResourceMount( String domain, String subPath );

    InputStream createResourceFile( String domain, String subPath );
}
