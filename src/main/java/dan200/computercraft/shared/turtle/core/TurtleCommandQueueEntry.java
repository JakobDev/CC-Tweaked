/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.api.turtle.ITurtleCommand;

public class TurtleCommandQueueEntry
{
    public final int callbackID;
    public final ITurtleCommand command;

    public TurtleCommandQueueEntry( int callbackID, ITurtleCommand command )
    {
        this.callbackID = callbackID;
        this.command = command;
    }
}
