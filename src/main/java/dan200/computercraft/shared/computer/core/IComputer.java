/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.computer.core;

import dan200.computercraft.shared.common.ITerminal;
import dan200.computercraft.shared.computer.blocks.ComputerState;

public interface IComputer extends ITerminal, InputHandler
{
    int getInstanceID();

    @Deprecated
    int getID();

    @Deprecated
    String getLabel();

    boolean isOn();

    boolean isCursorDisplayed();

    void turnOn();

    void shutdown();

    void reboot();

    @Override
    void queueEvent( String event, Object[] arguments );

    default void queueEvent( String event )
    {
        queueEvent( event, null );
    }

    default ComputerState getState()
    {
        if( !isOn() ) return ComputerState.Off;
        return isCursorDisplayed() ? ComputerState.Blinking : ComputerState.On;
    }
}
