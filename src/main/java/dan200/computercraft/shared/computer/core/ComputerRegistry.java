/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.computer.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ComputerRegistry<TComputer extends IComputer>
{
    private Map<Integer, TComputer> m_computers;
    private int m_nextUnusedInstanceID;
    private int m_sessionID;

    protected ComputerRegistry()
    {
        m_computers = new HashMap<>();
        reset();
    }

    public int getSessionID()
    {
        return m_sessionID;
    }

    public int getUnusedInstanceID()
    {
        return m_nextUnusedInstanceID++;
    }

    public Collection<TComputer> getComputers()
    {
        return m_computers.values();
    }

    public TComputer get( int instanceID )
    {
        if( instanceID >= 0 )
        {
            if( m_computers.containsKey( instanceID ) )
            {
                return m_computers.get( instanceID );
            }
        }
        return null;
    }

    public boolean contains( int instanceID )
    {
        return m_computers.containsKey( instanceID );
    }

    public void add( int instanceID, TComputer computer )
    {
        if( m_computers.containsKey( instanceID ) )
        {
            remove( instanceID );
        }
        m_computers.put( instanceID, computer );
        m_nextUnusedInstanceID = Math.max( m_nextUnusedInstanceID, instanceID + 1 );
    }

    public void remove( int instanceID )
    {
        m_computers.remove( instanceID );
    }

    public void reset()
    {
        m_computers.clear();
        m_nextUnusedInstanceID = 0;
        m_sessionID = new Random().nextInt();
    }
}
