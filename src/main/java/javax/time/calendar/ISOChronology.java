/*
 * Copyright (c) 2007-2011, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar;

import static javax.time.calendar.ISODateTimeRule.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.ALIGNED_WEEK_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.AMPM_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.CLOCK_HOUR_OF_AMPM;
import static javax.time.calendar.ISODateTimeRule.CLOCK_HOUR_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendar.ISODateTimeRule.DAY_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.EPOCH_DAY;
import static javax.time.calendar.ISODateTimeRule.EPOCH_MONTH;
import static javax.time.calendar.ISODateTimeRule.EPOCH_SECOND;
import static javax.time.calendar.ISODateTimeRule.EPOCH_YEAR;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_AMPM;
import static javax.time.calendar.ISODateTimeRule.HOUR_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MILLI_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MILLI_OF_HOUR;
import static javax.time.calendar.ISODateTimeRule.MILLI_OF_MINUTE;
import static javax.time.calendar.ISODateTimeRule.MILLI_OF_SECOND;
import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.MINUTE_OF_HOUR;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_QUARTER;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.NANO_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.NANO_OF_HOUR;
import static javax.time.calendar.ISODateTimeRule.NANO_OF_MILLI;
import static javax.time.calendar.ISODateTimeRule.NANO_OF_MINUTE;
import static javax.time.calendar.ISODateTimeRule.NANO_OF_SECOND;
import static javax.time.calendar.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_DAY;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_HOUR;
import static javax.time.calendar.ISODateTimeRule.SECOND_OF_MINUTE;
import static javax.time.calendar.ISODateTimeRule.WEEK_BASED_YEAR;
import static javax.time.calendar.ISODateTimeRule.WEEK_OF_WEEK_BASED_YEAR;
import static javax.time.calendar.ISODateTimeRule.YEAR;

import java.io.Serializable;

import javax.time.MathUtils;

/**
 * The ISO-8601 calendar system, which follows the rules of the current
 * <i>de facto</i> world calendar.
 * <p>
 * ISOChronology follows the rules of the Gregorian calendar for all time.
 * Thus, dates is the past, and particularly before 1583, may not correspond
 * to historical documents.
 * <p>
 * ISOChronology is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class ISOChronology extends Chronology implements Serializable {

    /**
     * The singleton instance of {@code ISOChronology}.
     */
    public static final ISOChronology INSTANCE = new ISOChronology();
    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The number of seconds in one day.
     */
    static final int SECONDS_PER_DAY = 60 * 60 * 24;
    /**
     * The number of days in a 400 year cycle.
     */
    static final int DAYS_PER_CYCLE = 146097;
    /**
     * The number of days from year zero to year 1970.
     * There are five 400 year cycles from year zero to 2000.
     * There are 7 leap years from 1970 to 2000.
     */
    static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);
    /**
     * The number of days from year zero to the Modified Julian Day epoch of 1858-11-17.
     */
    static final long DAYS_0000_TO_MJD_EPOCH = 678941;

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year according to the ISO calendar system rules.
     * <p>
     * The ISO calendar system applies the current rules for leap years across the whole time-line.
     * In general, a year is a leap year if it is divisible by four without
     * remainder. However, years divisible by 100, are not leap years, with
     * the exception of years divisible by 400 which are.
     * <p>
     * For example, 1904 is a leap year it is divisible by 4.
     * 1900 was not a leap year as it is divisible by 100, however 2000 was a
     * leap year as it is divisible by 400.
     * <p>
     * The calculation is proleptic - applying the same rules into the far future and far past.
     * This is historically inaccurate, but is correct for the ISO8601 standard.
     *
     * @param year  the year to check, may be outside the valid range for the rule
     * @return true if the year is a leap year
     */
    public static boolean isLeapYear(long year) {
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }

    //-----------------------------------------------------------------------
    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the day-of-week from a date.
     *
     * @param date  the date to use, not null
     * @return the day-of-week
     */
    static DayOfWeek getDayOfWeekFromDate(LocalDate date) {
        long mjd = date.toModifiedJulianDay();
        if (mjd < 0) {
            long weeks = mjd / 7;
            mjd += (-weeks + 1) * 7;
        }
        int dow0 = (int) ((mjd + 2) % 7);
        return DayOfWeek.of(dow0 + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the day-of-year from a date.
     *
     * @param date  the date to use, not null
     * @return the day-of-year
     */
    static int getDayOfYearFromDate(LocalDate date) {
        return date.getMonthOfYear().getMonthStartDayOfYear(date.isLeapYear()) + date.getDayOfMonth() - 1;
    }

    /**
     * Calculates the date from a year and day-of-year.
     *
     * @param year  the year, valid
     * @param dayOfYear  the day-of-year, valid
     * @return the date, not null
     */
    static LocalDate getDateFromDayOfYear(int year, int dayOfYear) {
        DAY_OF_YEAR.checkValidValue(dayOfYear);
        boolean leap = ISOChronology.isLeapYear(year);
        if (dayOfYear == 366 && leap == false) {
            throw new InvalidCalendarFieldException("DayOfYear 366 is invalid for year " + year, DAY_OF_YEAR);
        }
        MonthOfYear moy = MonthOfYear.of((dayOfYear - 1) / 31 + 1);
        int monthEnd = moy.getMonthEndDayOfYear(leap);
        if (dayOfYear > monthEnd) {
            moy = moy.next();
        }
        int dom = dayOfYear - moy.getMonthStartDayOfYear(leap) + 1;
        return LocalDate.of(year, moy, dom);
    }

    //-----------------------------------------------------------------------
    /**
     * Calculates the week-based-year.
     *
     * @param date  the date, not null
     * @return the week-based-year
     */
    static int getWeekBasedYearFromDate(LocalDate date) {
        Year year = Year.of(date);  // use ISO year object so previous/next are checked
        if (date.getMonthOfYear() == MonthOfYear.JANUARY) {
            int dom = date.getDayOfMonth();
            if (dom < 4) {
                int dow = date.getDayOfWeek().getValue();
                if (dow > dom + 3) {
                    year = year.previous();
                }
            }
        } else if (date.getMonthOfYear() == MonthOfYear.DECEMBER) {
            int dom = date.getDayOfMonth();
            if (dom > 28) {
                int dow = date.getDayOfWeek().getValue();
                if (dow <= dom % 7) {
                    year = year.next();
                }
            }
        }
        return year.getValue();
    }

    /**
     * Calculates the week of week-based-year.
     *
     * @param date  the date to use, not null
     * @return the week
     */
    static int getWeekOfWeekBasedYearFromDate(LocalDate date) {
        int wby = getWeekBasedYearFromDate(date);
        LocalDate yearStart = LocalDate.of(wby, MonthOfYear.JANUARY, 4);
        return MathUtils.safeToInt((date.toModifiedJulianDay() - yearStart.toModifiedJulianDay() +
                yearStart.getDayOfWeek().getValue() - 1) / 7 + 1);
    }

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private ISOChronology() {
    }

    /**
     * Resolves singleton.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology.
     *
     * @return the name of the chronology, not null
     */
    @Override
    public String getName() {
        return "ISO";
    }

    //-----------------------------------------------------------------------
//    /**
//     * Gets the equivalent rule for the specified field in the ISO chronology.
//     * <p>
//     * This will take the input field and provide the closest matching field
//     * that is based......
//     *
//     * @param rule  the rule to convert, not null
//     * @return the rule in ISO chronology, not null
//     */
//    public DateTimeRule<?> convertRule(DateTimeRule<?> rule) {
//        if (rule.getChronology().equals(this)) {
//            return rule;
//        }
//        return null;
////        return rule.getChronology().convertRule(rule);;
//    }

    //-----------------------------------------------------------------------
    /**
     * Merges the set of fields known by this chronology.
     *
     * @param merger  the merger to use, not null
     */
    void merge(CalendricalMerger merger) {
        normalizeSplit(merger, EPOCH_SECOND, SECOND_OF_DAY, EPOCH_DAY, 86400);
        normalizeSplit(merger, EPOCH_MONTH, MONTH_OF_YEAR, EPOCH_YEAR, 12);
        
        normalizeConvert(merger, CLOCK_HOUR_OF_AMPM, HOUR_OF_AMPM);
        normalizeConvert(merger, CLOCK_HOUR_OF_DAY, HOUR_OF_DAY);
        normalizeConvert(merger, EPOCH_YEAR, YEAR);
        
        normalizeMerge(merger, HOUR_OF_AMPM, AMPM_OF_DAY, HOUR_OF_DAY);
        
        normalizeMerge(merger, NANO_OF_MILLI, MILLI_OF_DAY, NANO_OF_DAY);
        normalizeMerge(merger, NANO_OF_MILLI, MILLI_OF_HOUR, NANO_OF_HOUR);
        normalizeMerge(merger, NANO_OF_MILLI, MILLI_OF_MINUTE, NANO_OF_MINUTE);
        normalizeMerge(merger, NANO_OF_MILLI, MILLI_OF_SECOND, NANO_OF_SECOND);
        
        normalizeMerge(merger, NANO_OF_SECOND, SECOND_OF_DAY, NANO_OF_DAY);
        normalizeMerge(merger, NANO_OF_SECOND, SECOND_OF_HOUR, NANO_OF_HOUR);
        normalizeMerge(merger, NANO_OF_SECOND, SECOND_OF_MINUTE, NANO_OF_MINUTE);
        
        normalizeMerge(merger, NANO_OF_MINUTE, MINUTE_OF_DAY, NANO_OF_DAY);
        normalizeMerge(merger, NANO_OF_MINUTE, MINUTE_OF_HOUR, NANO_OF_HOUR);
        
        normalizeMerge(merger, NANO_OF_HOUR, HOUR_OF_DAY, NANO_OF_DAY);
        
        normalizeMerge(merger, MILLI_OF_SECOND, SECOND_OF_DAY, MILLI_OF_DAY);
        normalizeMerge(merger, MILLI_OF_SECOND, SECOND_OF_HOUR, MILLI_OF_HOUR);
        normalizeMerge(merger, MILLI_OF_SECOND, SECOND_OF_MINUTE, MILLI_OF_MINUTE);
        
        normalizeMerge(merger, MILLI_OF_MINUTE, MINUTE_OF_DAY, MILLI_OF_DAY);
        normalizeMerge(merger, MILLI_OF_MINUTE, MINUTE_OF_HOUR, MILLI_OF_HOUR);
        
        normalizeMerge(merger, MILLI_OF_HOUR, HOUR_OF_DAY, MILLI_OF_DAY);
        
        normalizeMerge(merger, SECOND_OF_MINUTE, MINUTE_OF_DAY, SECOND_OF_DAY);
        normalizeMerge(merger, SECOND_OF_MINUTE, MINUTE_OF_HOUR, SECOND_OF_HOUR);
        
        normalizeMerge(merger, SECOND_OF_HOUR, HOUR_OF_DAY, SECOND_OF_DAY);
        
        normalizeMerge(merger, MINUTE_OF_HOUR, HOUR_OF_DAY, MINUTE_OF_DAY);
        
        normalizeMerge(merger, MONTH_OF_QUARTER, QUARTER_OF_YEAR, MONTH_OF_YEAR);
        
        // TODO: add "fallback fields" concept to merger
        // nano-of-day
        DateTimeField nodVal = merger.getValue(NANO_OF_DAY);
        if (nodVal != null) {
            merger.storeMerged(LocalTime.rule(), LocalTime.ofNanoOfDay(nodVal.getValidValue()));
            merger.removeProcessed(NANO_OF_DAY);
        }
        
        // milli-of-day
        DateTimeField modVal = merger.getValue(MILLI_OF_DAY);
        if (modVal != null) {
            merger.storeMerged(LocalTime.rule(), LocalTime.ofNanoOfDay(modVal.getValidIntValue() * 1000000L));
            merger.removeProcessed(MILLI_OF_DAY);
        }
        
        // second-of-day
        DateTimeField sodVal = merger.getValue(SECOND_OF_DAY);
        if (sodVal != null) {
            merger.storeMerged(LocalTime.rule(), LocalTime.ofSecondOfDay(sodVal.getValidIntValue()));
            merger.removeProcessed(SECOND_OF_DAY);
        }
        
        // minute-of-day
        DateTimeField minodVal = merger.getValue(MINUTE_OF_DAY);
        if (minodVal != null) {
            merger.storeMerged(LocalTime.rule(), LocalTime.ofSecondOfDay(minodVal.getValidIntValue() * 60L));
            merger.removeProcessed(MINUTE_OF_DAY);
        }
        
        // hour-of-day
        DateTimeField hodVal = merger.getValue(HOUR_OF_DAY);
        if (hodVal != null) {
            merger.storeMerged(LocalTime.rule(), LocalTime.of(hodVal.getValidIntValue(), 0));
            merger.removeProcessed(HOUR_OF_DAY);
        }
        
        // year
        DateTimeField yearVal = merger.getValue(YEAR);
        if (yearVal != null) {
            // year-month-day
            DateTimeField moy = merger.getValue(MONTH_OF_YEAR);
            DateTimeField domVal = merger.getValue(DAY_OF_MONTH);
            if (moy != null && domVal != null) {
                LocalDate date = merger.getContext().resolveDate(yearVal.getValidIntValue(), moy.getValidIntValue(), domVal.getValidIntValue());
                merger.storeMerged(LocalDate.rule(), date);
                merger.removeProcessed(YEAR);
                merger.removeProcessed(MONTH_OF_YEAR);
                merger.removeProcessed(DAY_OF_MONTH);
            }
            // year-day
            DateTimeField doyVal = merger.getValue(DAY_OF_YEAR);
            if (doyVal != null) {
                merger.storeMerged(LocalDate.rule(), getDateFromDayOfYear(yearVal.getValidIntValue(), doyVal.getValidIntValue()));
                merger.removeProcessed(YEAR);
                merger.removeProcessed(DAY_OF_YEAR);
            }
            // year-week-day
            DateTimeField woyVal = merger.getValue(ALIGNED_WEEK_OF_YEAR);
            DateTimeField dow = merger.getValue(DAY_OF_WEEK);
            if (woyVal != null && dow != null) {
                LocalDate date = LocalDate.of(yearVal.getValidIntValue(), 1, 1).plusWeeks(woyVal.getValidIntValue() - 1);
                date = date.with(DateAdjusters.nextOrCurrent(DayOfWeek.of(dow.getValidIntValue())));
                merger.storeMerged(LocalDate.rule(), date);
                merger.removeProcessed(YEAR);
                merger.removeProcessed(ALIGNED_WEEK_OF_YEAR);
                merger.removeProcessed(DAY_OF_WEEK);
            }
            // year-month-week-day
            DateTimeField womVal = merger.getValue(ALIGNED_WEEK_OF_MONTH);
            if (moy != null && womVal != null && dow != null) {
                LocalDate date = LocalDate.of(yearVal.getValidIntValue(), moy.getValidIntValue(), 1).plusWeeks(womVal.getValidIntValue() - 1);
                date = date.with(DateAdjusters.nextOrCurrent(DayOfWeek.of(dow.getValidIntValue())));
                merger.storeMerged(LocalDate.rule(), date);
                merger.removeProcessed(YEAR);
                merger.removeProcessed(MONTH_OF_YEAR);
                merger.removeProcessed(ALIGNED_WEEK_OF_MONTH);
                merger.removeProcessed(DAY_OF_WEEK);
            }
        }
        
        // weekyear-week-day
        DateTimeField wbyVal = merger.getValue(WEEK_BASED_YEAR);
        if (wbyVal != null) {
            DateTimeField woy = merger.getValue(WEEK_OF_WEEK_BASED_YEAR);
            DateTimeField dow = merger.getValue(DAY_OF_WEEK);
            if (woy != null && dow != null) {
                // TODO: implement
                merger.removeProcessed(WEEK_BASED_YEAR);
                merger.removeProcessed(WEEK_OF_WEEK_BASED_YEAR);
                merger.removeProcessed(DAY_OF_WEEK);
            }
        }
        
        // epoch-day
        DateTimeField edVal = merger.getValue(EPOCH_DAY);
        if (edVal != null) {
            merger.storeMerged(LocalDate.rule(), LocalDate.ofEpochDay(edVal.getValidValue()));
            merger.removeProcessed(EPOCH_DAY);
        }
        
        // LocalDateTime
        LocalDate date = merger.getValue(LocalDate.rule());
        LocalTime time = merger.getValue(LocalTime.rule());
        ZoneOffset offset = merger.getValue(ZoneOffset.rule());
        ZoneId zone = merger.getValue(ZoneId.rule());
        if (date != null && time != null) {
            merger.storeMerged(LocalDateTime.rule(), LocalDateTime.of(date, time));
            merger.removeProcessed(LocalDate.rule());
            merger.removeProcessed(LocalTime.rule());
        }
        
        // OffsetDate
        if (date != null && offset != null) {
            merger.storeMerged(OffsetDate.rule(), OffsetDate.of(date, offset));
            merger.removeProcessed(LocalDate.rule());
            merger.removeProcessed(ZoneOffset.rule());
        }
        
        // OffsetTime
        if (time != null && offset != null) {
            merger.storeMerged(OffsetTime.rule(), OffsetTime.of(time, offset));
            merger.removeProcessed(LocalTime.rule());
            merger.removeProcessed(ZoneOffset.rule());
        }
        
        // OffsetDateTime
        LocalDateTime ldt = merger.getValue(LocalDateTime.rule());
        if (ldt != null && offset != null) {
            merger.storeMerged(OffsetDateTime.rule(), OffsetDateTime.of(ldt, offset));
            merger.removeProcessed(LocalDateTime.rule());
            merger.removeProcessed(ZoneOffset.rule());
        } else {
            OffsetDate od = merger.getValue(OffsetDate.rule());
            OffsetTime ot = merger.getValue(OffsetTime.rule());
            if (od != null && ot != null) {
                if (od.getOffset().equals(ot.getOffset()) == false) {
                    if (merger.getContext().isStrict()) {
                        throw new CalendricalRuleException("Unable to merge OffsetDate and OffsetTime as offsets differ", OffsetTime.rule());
                    } else {
                        // TODO test
                        ot = ot.withOffsetSameInstant(od.getOffset());
                    }
                }
                merger.storeMerged(OffsetDateTime.rule(), OffsetDateTime.of(od, ot, od.getOffset()));
                merger.removeProcessed(OffsetDate.rule());
                merger.removeProcessed(OffsetTime.rule());
            }
        }
        
        // ZonedDateTime
        OffsetDateTime odt = merger.getValue(OffsetDateTime.rule());
        if (odt != null && zone != null) {
            if (merger.getContext().isStrict()) {
                merger.storeMerged(ZonedDateTime.rule(), ZonedDateTime.of(odt, zone));
            } else {
                merger.storeMerged(ZonedDateTime.rule(), ZonedDateTime.ofInstant(odt, zone));
            }
            merger.removeProcessed(OffsetDateTime.rule());
            merger.removeProcessed(ZoneId.rule());
        }
    }

    /**
     * Merges a field to a simpler form of itself.
     * <p>
     * This is used when two rules are alternate views of the same concept.
     * The classic example of this is converting clock-hour-of-ampm to hour-of-ampm.
     * 
     * @param merger  the merger instance, not null
     * @param sourceRule  the rule to merge, not null
     * @param destRule  the rule to merge into, not null
     */
    protected void normalizeConvert(CalendricalMerger merger, DateTimeRule sourceRule, DateTimeRule destRule) {
        DateTimeField field1 = merger.getValue(sourceRule);
        if (field1 != null) {
            long period = sourceRule.convertToPeriod(field1.getValue());
            merger.storeMergedField(destRule, destRule.convertFromPeriod(period));
            merger.removeProcessed(sourceRule);
        }
    }

    /**
     * Merges two fields to form another.
     * <p>
     * This is used to combine two fields into a larger whole.
     * For example, merge hour-of-ampm and ampm-of-day to form hour-of-day.
     * <p>
     * The source rules must be related by {@link PeriodUnit#getEquivalentPeriod}.
     * 
     * @param merger  the merger instance, not null
     * @param ruleSmaller  the smaller rule to merge - SmallOfMid, not null
     * @param ruleLarger  the larger rule to merge - MidOfBig, not null
     * @param destRule  the rule to merge into - SmallOfBig, not null
     */
    protected void normalizeMerge(CalendricalMerger merger, DateTimeRule ruleSmaller, DateTimeRule ruleLarger, DateTimeRule destRule) {
        DateTimeField field1 = merger.getValue(ruleLarger);
        if (field1 != null) {
            DateTimeField field2 = merger.getValue(ruleSmaller);
            if (field2 != null) {
                long period1 = ruleLarger.convertToPeriod(field1.getValidValue());  // TODO: strict/lenient
                long period2 = ruleSmaller.convertToPeriod(field2.getValidValue());
                PeriodField conversion = ruleLarger.getPeriodUnit().getEquivalentPeriod(ruleSmaller.getPeriodUnit());
                long scaledPeriod1 = MathUtils.safeMultiply(period1, conversion.getAmount());
                long totalPeriod = MathUtils.safeAdd(scaledPeriod1, period2);
                merger.storeMergedField(destRule, destRule.convertFromPeriod(totalPeriod));
                merger.removeProcessed(ruleLarger);
                merger.removeProcessed(ruleSmaller);
            }
        }
    }

    /**
     * Splits a field to create two fields.
     * <p>
     * This is used to split one field to form two partial fields.
     * For example, split epoch-month to form month-of-year and epoch-year.
     * <p>
     * A conversion is passed in rather than using the equivalent period.
     * This handles cases across the day boundary.
     * 
     * @param merger  the merger instance, not null
     * @param sourceRule  the rule to split - SmallOfBig, not null
     * @param smallerRule  the smaller rule to merge - SmallOfMid, not null
     * @param largerRule  the larger rule to merge - MidOfBig, not null
     * @param conversion  the conversion factor to use
     */
    protected void normalizeSplit(CalendricalMerger merger, DateTimeRule sourceRule, DateTimeRule smallerRule, DateTimeRule largerRule, long conversion) {
        DateTimeField field = merger.getValue(sourceRule);
        if (field != null) {
            long period = largerRule.convertToPeriod(field.getValue());
            long largerPeriod = MathUtils.floorDiv(period, conversion);
            long smallerPeriod = MathUtils.floorMod(period, conversion);
            merger.storeMergedField(largerRule, largerRule.convertFromPeriod(largerPeriod));
            merger.storeMergedField(smallerRule, smallerRule.convertFromPeriod(smallerPeriod));
            merger.removeProcessed(sourceRule);
        }
    }

}
