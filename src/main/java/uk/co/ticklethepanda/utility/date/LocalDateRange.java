package uk.co.ticklethepanda.utility.date;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Range is not inclusive of last date
 */
public class LocalDateRange implements Iterable<LocalDate> {
    private final LocalDate start;
    private final LocalDate end;

    public LocalDateRange(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    /**
     * Inclusive of start, exclusive of end.
     * @param date the date to check for
     * @return whether the date is contained
     */
    public boolean contains(LocalDate date) {
        return date.isEqual(start) || (date.isAfter(start) && date.isBefore(end));
    }

    @Override
    public Iterator<LocalDate> iterator() {
        return new LocalDateRangeIterator();
    }

    private class LocalDateRangeIterator implements Iterator<LocalDate> {
        private LocalDate current = start;

        @Override
        public boolean hasNext() {
            return this.current != null;
        }

        @Override
        public LocalDate next() {
            if (this.current == null) {
                throw new NoSuchElementException();
            }
            final LocalDate ret = this.current;
            this.current = this.current.plusDays(1);

            if (this.current.isBefore(end)) {
                this.current = null;
            }
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}