open module net.cassite.hottapcassistant {
    requires kotlin.stdlib;
    requires io.vproxy.base;
    requires io.vproxy.vfx;
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
    requires net.cassite.xboxrelay.ui;
    requires org.pcap4j.core;
    requires net.cassite.tofpcap;
    requires org.controlsfx.controls;
    requires io.vproxy.windivert;
    requires io.vproxy.windivert.hostsmanager;
    requires io.vproxy.pni;
    exports net.cassite.hottapcassistant;
    exports net.cassite.hottapcassistant.test;
}
