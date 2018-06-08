package ru.glaizier.algds.alg.functional.streamforker.java8inaction;

/**
 * @author GlaIZier
 */
public interface ForkResult {
    <R> R get(Object key);
}
