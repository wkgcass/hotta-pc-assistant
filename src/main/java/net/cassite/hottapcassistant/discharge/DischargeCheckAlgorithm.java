package net.cassite.hottapcassistant.discharge;

public interface DischargeCheckAlgorithm {
    void init(DischargeCheckContext ctx);

    DischargeCheckResult check();

    record DischargeCheckResult(double p, double pMax) {
    }
}
