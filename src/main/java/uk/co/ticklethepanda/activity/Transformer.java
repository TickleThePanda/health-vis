package uk.co.ticklethepanda.activity;

/**
 *
 */
public interface Transformer<I, O> {
    O transform(I input);
}
