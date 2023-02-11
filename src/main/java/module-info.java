open module net.cassite.hottapcassistant {
    requires io.vproxy.vfx;
    requires kotlin.stdlib;
    requires javafx.controls;
    requires javafx.swing;
    requires javafx.media;
    requires java.desktop;
    requires java.net.http;
    requires jdk.crypto.ec;
    requires vjson;
    requires com.github.kwhat.jnativehook;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires io.vertx.core;
    requires org.slf4j;
    exports net.cassite.hottapcassistant;
    exports net.cassite.hottapcassistant.test;
}
