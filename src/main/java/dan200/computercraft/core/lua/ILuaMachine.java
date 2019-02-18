/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.core.lua;

import dan200.computercraft.api.lua.ILuaAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;

public interface ILuaMachine
{
    void addAPI( ILuaAPI api );

    void loadBios( InputStream bios );

    /**
     * Handle a Lua event
     *
     * @param eventName The events' name, or {@code null} if resuming the machine
     * @param arguments The arguments for this event
     * @return Whether this machine was paused.
     */
    boolean handleEvent( @Nullable String eventName, @Nullable Object[] arguments );

    /**
     * Tell the machine to pause itself.
     *
     * There is no guarantee as to when the machine will actually be paused: this generally sets a flag which causes the
     * machine to be suspended when the next opportunity arises.
     */
    void pause();

    void softAbort( @Nonnull String abortMessage );

    void hardAbort( @Nonnull String abortMessage );

    boolean isFinished();

    void close();
}
