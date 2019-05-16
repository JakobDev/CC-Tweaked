/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.computer.core;

/**
 * Receives some input and forwards it to a computer
 *
 * @see InputState
 * @see IComputer
 */
public interface InputHandler
{
    void queueEvent( String event, Object[] arguments );

    default void keyDown( int key, boolean repeat )
    {
        queueEvent( "key", new Object[] { key, repeat } );
    }

    default void keyUp( int key )
    {
        queueEvent( "key_up", new Object[] { key } );
    }

    default void mouseClick( int button, int x, int y )
    {
        queueEvent( "mouse_click", new Object[] { button, x, y } );
    }

    default void mouseUp( int button, int x, int y )
    {
        queueEvent( "mouse_up", new Object[] { button, x, y } );
    }

    default void mouseDrag( int button, int x, int y )
    {
        queueEvent( "mouse_drag", new Object[] { button, x, y } );
    }

    default void mouseScroll( int direction, int x, int y )
    {
        queueEvent( "mouse_scroll", new Object[] { direction, x, y } );
    }
}
