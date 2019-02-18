/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.core.lua;

import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.core.computer.Computer;
import dan200.computercraft.core.computer.TimeoutFlags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.function.BiFunction;

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
     * If this machine has finished executing, either due to an error or falling off the stack
     *
     * @return The machine to finish executing.
     */
    boolean isFinished();

    /**
     * Dispose of this machine, releasing any resources
     */
    void close();

    /**
     * The global factory for {@link ILuaMachine}s.
     */
    final class Factory
    {
        public static BiFunction<Computer, TimeoutFlags, ILuaMachine> factory = CobaltLuaMachine::new;

        private Factory()
        {
        }

        /**
         * Construct a new machine from the given computer and timeout information.
         *
         * @param computer The current computer
         * @param flags    The timeout flags, these will be updated when the computer must be paused or stopped
         * @return The constructed machine
         */
        @Nonnull
        public static ILuaMachine create( @Nonnull Computer computer, @Nonnull TimeoutFlags flags )
        {
            return factory.apply( computer, flags );
        }
    }
}
