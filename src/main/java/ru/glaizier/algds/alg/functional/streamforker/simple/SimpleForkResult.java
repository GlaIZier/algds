package ru.glaizier.algds.alg.functional.streamforker.simple;

/**
 * @author GlaIZier
 */
public interface SimpleForkResult {
    <R> R get(Object key);
}
