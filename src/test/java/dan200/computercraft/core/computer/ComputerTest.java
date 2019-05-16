/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.core.computer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class ComputerTest
{
    @Test
    public void testTimeout()
    {
        assertTimeoutPreemptively( ofSeconds( 20 ), () -> {
            try
            {
                ComputerBootstrap.run( "print('Hello') while true do end" );
            }
            catch( AssertionError e )
            {
                if( e.getMessage().equals( "test.lua:1: Too long without yielding" ) ) return;
                throw e;
            }

            Assertions.fail( "Expected computer to timeout" );
        } );
    }
}
