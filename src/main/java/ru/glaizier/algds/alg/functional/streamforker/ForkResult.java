package ru.glaizier.algds.alg.functional.streamforker;

/**
 * @author GlaIZier
 */
public interface ForkResult {
    <R> R get(Object key);
}
