package radium;

import radium.annotations.NotNull;

/**
 * Created by plozano on 11/22/2016.
 */
public interface Comparable<T> {

    @NotNull
    Int compareTo(@NotNull T t);
}
