package com.saphienyako.quest_giver.quest.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

//mall utility to delay instantiation of QuestData until it’s actually needed
//prevents issues where the player isn’t fully initialized yet
public class LazyValue<T> {

    private Supplier<? extends T> supplier;
    private T value;
    private boolean computed = false;

    public LazyValue(Supplier<? extends T> supplier) {
        this.supplier = Objects.requireNonNull(supplier, "Supplier cannot be null");
    }

    public T get() {
        if (!computed) {
            value = supplier.get();
            supplier = null;
            computed = true;
        }
        return value;
    }

    public <U> LazyValue<U> map(Function<T, U> mapper) {
        return new LazyValue<>(() -> mapper.apply(this.get()));
    }

}
