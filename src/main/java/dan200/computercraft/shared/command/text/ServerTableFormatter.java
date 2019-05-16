/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.command.text;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class ServerTableFormatter implements TableFormatter
{
    private final ICommandSender source;

    public ServerTableFormatter( ICommandSender source )
    {
        this.source = source;
    }

    @Override
    @Nullable
    public ITextComponent getPadding( ITextComponent component, int width )
    {
        int extraWidth = width - getWidth( component );
        if( extraWidth <= 0 ) return null;
        return new TextComponentString( StringUtils.repeat( ' ', extraWidth ) );
    }

    @Override
    public int getColumnPadding()
    {
        return 1;
    }

    @Override
    public int getWidth( ITextComponent component )
    {
        return component.getUnformattedText().length();
    }

    @Override
    public void writeLine( int id, ITextComponent component )
    {
        source.sendMessage( component );
    }
}
