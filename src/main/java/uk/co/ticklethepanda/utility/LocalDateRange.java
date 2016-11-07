package uk.co.ticklethepanda.utility;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LocalDateRange implements Iterable<LocalDate> {
  private static class LocalDateRangeIterator implements Iterator<LocalDate> {
    private LocalDate current;
    private final LocalDate end;

    private LocalDateRangeIterator(LocalDate start, LocalDate end) {
      this.current = start;
      this.end = end;
    }

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
      if (this.current.compareTo(this.end) > 0) {
        this.current = null;
      }
      return ret;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private final LocalDate start;

  private final LocalDate end;

  public LocalDateRange(LocalDate start, LocalDate end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public Iterator<LocalDate> iterator() {
    return new LocalDateRangeIterator(this.start, this.end);
  }
}