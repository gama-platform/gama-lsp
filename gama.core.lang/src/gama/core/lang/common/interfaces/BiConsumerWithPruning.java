package gama.core.lang.common.interfaces;

public interface BiConsumerWithPruning<K, V> {
	boolean process(K k, V v);
}