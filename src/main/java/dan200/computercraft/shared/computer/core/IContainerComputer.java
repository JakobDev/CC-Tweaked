/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.computer.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An instance of {@link net.minecraft.inventory.Container} which provides a computer. You should implement this
 * if you provide custom computers/GUIs to interact with them.
 */
@FunctionalInterface
public interface IContainerComputer
{
    /**
     * Get the computer you are interacting with.
     *
     * This will only be called on the server.
     *
     * @return The computer you are interacting with.
     */
    @Nullable
    IComputer getComputer();

    /**
     * Get the input controller for this container.
     *
     * @return This container's input.
     */
    @Nonnull
    default InputState getInput()
    {
        return new InputState( this );
    }
}
