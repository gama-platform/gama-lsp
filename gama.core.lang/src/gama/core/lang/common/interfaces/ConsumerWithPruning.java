package gama.core.lang.common.interfaces;

public interface ConsumerWithPruning<T> {

	boolean process(T t);

}
