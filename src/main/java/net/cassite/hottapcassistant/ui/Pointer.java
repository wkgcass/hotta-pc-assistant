package net.cassite.hottapcassistant.ui;

public class Pointer<T> {
    public final T item;

    private Pointer(T item) {
        this.item = item;
    }

    public static <T> Pointer<T> of(T t) {
        return new Pointer<>(t);
    }
}
