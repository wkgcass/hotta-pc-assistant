package net.cassite.hottapcassistant.entity;

import net.cassite.hottapcassistant.util.Utils;
import vjson.JSON;
import vjson.deserializer.rule.*;
import vjson.util.ObjectBuilder;

public abstract class AssistantMacroStep {
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public static final Rule<AssistantMacroStep> rule = new TypeRule<AssistantMacroStep>()
        .type("KeyPress", KeyPress.rule)
        .type("KeyRelease", KeyRelease.rule)
        .type("Delay", Delay.rule);

    public abstract JSON.Object toJson();

    public abstract void exec();

    public static class KeyPress extends AssistantMacroStep {
        public Key key;

        public static final ObjectRule<KeyPress> rule = new ObjectRule<>(KeyPress::new)
            .put("key", (o, it) -> o.key = new Key(it), StringRule.get());

        public KeyPress() {
        }

        public KeyPress(Key key) {
            this.key = key;
        }

        @Override
        public JSON.Object toJson() {
            return new ObjectBuilder()
                .type("KeyPress")
                .put("key", key.toString())
                .build();
        }

        @Override
        public void exec() {
            Utils.execRobot(r -> r.press(key));
        }
    }

    public static class KeyRelease extends AssistantMacroStep {
        public Key key;

        public static final ObjectRule<KeyRelease> rule = new ObjectRule<>(KeyRelease::new)
            .put("key", (o, it) -> o.key = new Key(it), StringRule.get());

        public KeyRelease() {
        }

        public KeyRelease(Key key) {
            this.key = key;
        }

        @Override
        public JSON.Object toJson() {
            return new ObjectBuilder()
                .type("KeyRelease")
                .put("key", key.toString())
                .build();
        }

        @Override
        public void exec() {
            Utils.execRobot(r -> r.release(key));
        }
    }

    public static class Delay extends AssistantMacroStep {
        public int millis;

        public Delay() {
        }

        public Delay(int millis) {
            this.millis = millis;
        }

        public static final ObjectRule<Delay> rule = new ObjectRule<>(Delay::new)
            .put("millis", (o, it) -> o.millis = it, IntRule.get());

        @Override
        public JSON.Object toJson() {
            return new ObjectBuilder()
                .type("Delay")
                .put("millis", millis)
                .build();
        }

        @Override
        public void exec() {
            if (millis < 0) {
                return;
            }
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignore) {
            }
        }
    }
}
