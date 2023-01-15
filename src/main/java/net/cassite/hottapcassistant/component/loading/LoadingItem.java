package net.cassite.hottapcassistant.component.loading;

import java.util.function.BooleanSupplier;

public record LoadingItem(int weight, String name, BooleanSupplier loadFunc) {
    public LoadingItem(int weight, String name, Runnable loadFunc) {
        this(weight, name, () -> {
            loadFunc.run();
            return true;
        });
    }
}
