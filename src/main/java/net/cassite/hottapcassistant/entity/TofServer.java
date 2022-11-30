package net.cassite.hottapcassistant.entity;

public class TofServer {
    public boolean selected = false;
    public final String region;
    public final String name;
    public final String domain;
    public final String ip;

    public TofServer(String region, String name, String domain, String ip) {
        this.region = region;
        this.name = name;
        this.domain = domain;
        this.ip = ip;
    }
}
