package org.example;

import static java.lang.System.out;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.TimeZone;

@FunctionalInterface
interface TemporalAdjuster {
    Temporal adjustInto(Temporal temporal);
}

public class Chapter12 {
    public static void main(String... args) throws Exception {

        out.println(new Date(2023, 8, 18));
        out.println(new Date(123, 8, 18));

        var date = LocalDate.of(2023, 8, 18);
        out.println(date);
        out.println(LocalDate.now());

        out.println(date.getYear());
        out.println(date.getYear());

        var lt = LocalTime.now();
        lt.getHour();
        lt.getMinute();
        lt.getSecond();

        LocalDate.now();
        LocalTime.now();

        LocalDate.now().atTime(LocalTime.now());
        LocalTime.now().atDate(LocalDate.now());

        LocalDateTime.now().toLocalDate();
        LocalDateTime.now().toLocalTime();

        Duration threeMinutes = Duration.ofMinutes(3);
        //Duration threeMinutes = Duration.of(3, ChronoUnit.MINUTES);

        Period tenDays = Period.ofDays(10);
        Period threeWeeks = Period.ofWeeks(3);

        var date1 = LocalDate.of(2017, 9, 21);
        date1.withYear(2011);
        date1.withDayOfMonth(25);
        date1.withMonth(2);

        LocalDate.now()
                .with(nextOrSame(DayOfWeek.SUNDAY));

        LocalDate.now()
                .with(lastDayOfMonth());

        LocalDate.now()
                .with((temporal) -> {
                    var dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
                    var dayToAdd = 1;

                    if (dow == DayOfWeek.FRIDAY) {
                        dayToAdd = 3;
                    } else if (dow == DayOfWeek.SATURDAY) {
                        dayToAdd = 2;
                    }
                    return temporal.plus(dayToAdd, ChronoUnit.DAYS);
                });

        System.out.println(LocalDate.now()
                .atStartOfDay(TimeZone.getDefault().toZoneId()));


    }
}