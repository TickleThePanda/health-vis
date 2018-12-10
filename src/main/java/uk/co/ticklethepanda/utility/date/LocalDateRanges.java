package uk.co.ticklethepanda.utility.date;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LocalDateRanges implements Iterable<LocalDateRange> {

    public static class From {

        private LocalDate date;

        public static From from(LocalDate date) {
            return new From(date);
        }

        private From(LocalDate date) {
            this.date = date;
        }
    }

    public static class Until {
        private LocalDate date;

        public static Until until(LocalDate date) {
            return new Until(date);
        }

        private Until(LocalDate date) {
            this.date = date;
        }
    }

    public static LocalDateRanges every(int period, ChronoUnit unit, From from, Until until) {
        return new LocalDateRanges(from.date, until.date, period, unit);
    }

    private final LocalDate start;
    private final LocalDate end;
    private final int period;
    private final ChronoUnit unit;

    private LocalDateRanges(LocalDate start, LocalDate end, int period, ChronoUnit unit) {
        this.start = start;
        this.end = end;
        this.period = period;
        this.unit = unit;
    }

    @Override
    public Iterator<LocalDateRange> iterator() {
        return new Iterator<LocalDateRange>() {

            LocalDate indexDate = start;

            @Override
            public boolean hasNext() {
                return indexDate != null;
            }

            @Override
            public LocalDateRange next() {

                if(indexDate == null) {
                    throw new NoSuchElementException();
                }

                LocalDate next = indexDate.plus(period, unit);

                LocalDateRange range = new LocalDateRange(indexDate, next);

                indexDate = next;

                if(indexDate.isAfter(end)) {
                    indexDate = null;
                }

                return range;
            }
        };
    }
}
