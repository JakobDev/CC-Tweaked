/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.util;

import java.util.Calendar;

public final class HolidayUtil
{
    private HolidayUtil() {}

    public static Holiday getCurrentHoliday()
    {
        return getHoliday( Calendar.getInstance() );
    }

    private static Holiday getHoliday( Calendar calendar )
    {
        int month = calendar.get( Calendar.MONTH );
        int day = calendar.get( Calendar.DAY_OF_MONTH );
        if( month == Calendar.FEBRUARY && day == 14 ) return Holiday.Valentines;
        if( month == Calendar.APRIL && day == 1 ) return Holiday.AprilFoolsDay;
        if( month == Calendar.OCTOBER && day == 31 ) return Holiday.Halloween;
        if( month == Calendar.DECEMBER && day >= 24 && day <= 30 ) return Holiday.Christmas;
        return Holiday.None;
    }
}
