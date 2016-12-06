package uk.co.ticklethepanda.utility.web;

/**
 *
 */
public interface Transformer<I, O> {
    O transform(I input);
}
