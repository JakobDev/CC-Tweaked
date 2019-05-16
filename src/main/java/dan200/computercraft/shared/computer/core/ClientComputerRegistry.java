/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.computer.core;

public class ClientComputerRegistry extends ComputerRegistry<ClientComputer>
{
    public void update()
    {
        for( ClientComputer computer : getComputers() )
        {
            computer.update();
        }
    }

    @Override
    public void add( int instanceID, ClientComputer computer )
    {
        super.add( instanceID, computer );
        computer.requestState();
    }
}
