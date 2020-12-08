package com.stirante.justpipe.function;

import java.io.IOException;

@FunctionalInterface
public interface IOFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws IOException;

}
