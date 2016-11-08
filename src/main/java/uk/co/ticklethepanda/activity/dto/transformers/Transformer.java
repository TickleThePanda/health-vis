package uk.co.ticklethepanda.activity.dto.transformers;

/**
 * @author Lovingly hand crafted by the ISIS Business Applications Team
 */
public interface Transformer<I, O> {
    O transform(I input);
}
