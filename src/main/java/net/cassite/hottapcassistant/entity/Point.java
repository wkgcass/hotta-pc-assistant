package net.cassite.hottapcassistant.entity;

import vjson.JSON;
import vjson.deserializer.rule.DoubleRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;
import vjson.util.ObjectBuilder;

public class Point {
    public double x;
    public double y;

    public static final Rule<Point> rule = new ObjectRule<>(Point::new)
        .put("x", (o, it) -> o.x = it, DoubleRule.get())
        .put("y", (o, it) -> o.y = it, DoubleRule.get());

    public JSON.Object toJson() {
        return new ObjectBuilder()
            .put("x", x)
            .put("y", y)
            .build();
    }
}
