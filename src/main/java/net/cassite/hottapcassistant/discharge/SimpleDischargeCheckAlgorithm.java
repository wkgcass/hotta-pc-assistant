package net.cassite.hottapcassistant.discharge;

import java.util.function.Consumer;

@SuppressWarnings("UnusedAssignment")
public class SimpleDischargeCheckAlgorithm implements DischargeCheckAlgorithm {
    private DischargeCheckContext ctx;
    private final Consumer<String> logger;

    public static class Args {
        public int falsePathLimit = 5;
        public int jumpMovingPathLimit = falsePathLimit + 1;
        public int extraEnsure = 2;
    }

    public SimpleDischargeCheckAlgorithm() {
        this(new Args());
    }

    public SimpleDischargeCheckAlgorithm(Args args) {
        this(args, s -> {
        });
    }

    public SimpleDischargeCheckAlgorithm(Args args, Consumer<String> logger) {
        this.falsePathLimit = args.falsePathLimit;
        this.jumpMovingPathLimit = args.jumpMovingPathLimit;
        this.extraEnsure = args.extraEnsure;
        this.logger = logger;
    }

    @Override
    public void init(DischargeCheckContext ctx) {
        this.ctx = ctx;
    }

    private final int falsePathLimit;
    private final int jumpMovingPathLimit;
    private final int extraEnsure;

    @Override
    public DischargeCheckResult check() {
        var point = runStep1(ctx.getX());
        var p = ctx.calculatePercentage(point[0], point[1]);
        var min = p[0];
        var max = p[p.length - 1];
        return new DischargeCheckResult(min, max);
    }

    private int[] runStep1(int startX) {
        int lastX = ctx.getX();
        int lastY = ctx.getY();
        int state = 0; // 0 move right, 1 move down, 2 move left
        int downCount = 0;
        int leftCount = 0;
        while (true) {
            int moved;
            if ((moved = ctx.moveRightWithin(jumpMovingPathLimit, extraEnsure)) != -1) {
                lastX = ctx.getX();
                state = 0;
                downCount = 0;
                leftCount = 0;
                continue;
            }
            if ((moved = ctx.moveDownWithin(jumpMovingPathLimit - downCount, extraEnsure)) != -1) {
                downCount += moved;
                if (downCount > falsePathLimit) {
                    logger.accept("end step 1 enter step 2, too many down move: result = (" + lastX + ", " + lastY + ")");
                    return runStep2(startX);
                }

                lastY = ctx.getY();
                state = 1;
                leftCount = 0;
                continue;
            }
            if (state == 1 || state == 2) {
                if ((moved = ctx.moveLeftWithin(jumpMovingPathLimit - leftCount, extraEnsure)) != -1) {
                    leftCount += moved;
                    if (leftCount > falsePathLimit) {
                        logger.accept("finished at step 1, too many left move: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                        return new int[]{lastX, lastY};
                    }

                    state = 2;
                    continue;
                }
            }
            logger.accept("unable to go right, down or left, finished at step 1: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
            return new int[]{lastX, lastY};
        }
    }

    private int[] runStep2(int startX) {
        int lastX = ctx.getX();
        int lastY = ctx.getY();
        int state = 0; // 0 move down, 1 move left, 2 move up
        int leftCount = 0;
        int upCount = 0;
        while (true) {
            int moved;
            if ((moved = ctx.moveDownWithin(jumpMovingPathLimit, extraEnsure)) != -1) {
                lastY = ctx.getY();
                state = 0;
                leftCount = 0;
                upCount = 0;
                continue;
            }
            if ((moved = ctx.moveLeftWithin(jumpMovingPathLimit - leftCount, extraEnsure)) != -1) {
                leftCount += moved;
                if (leftCount > falsePathLimit) {
                    logger.accept("end step 2 enter step 3, too many left move: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                    return runStep3();
                }

                lastX = ctx.getX();
                state = 1;
                upCount = 0;
                continue;
            }
            if (state == 1 || state == 2) {
                if ((moved = ctx.moveUpWithin(jumpMovingPathLimit - upCount, extraEnsure)) != -1) {
                    upCount += moved;
                    if (upCount > falsePathLimit) {
                        logger.accept("end step 2 enter step 3, too many up move: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                        return runStep3();
                    }

                    state = 2;
                    continue;
                }
            }
            if (Math.abs(startX - ctx.getX()) < falsePathLimit) {
                logger.accept("end step 2 enter step 3, moved to bottom: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                return runStep3();
            }
            logger.accept("unable to go down, left or up, finished at step 2: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
            return new int[]{lastX, lastY};
        }
    }

    private int[] runStep3() {
        int lastX = ctx.getX();
        int lastY = ctx.getY();
        if (lastX > ctx.getInitialX() && lastX - ctx.getInitialX() > 10) {
            return new int[]{lastX, lastY};
        }
        int state = 0; // 0 move left, 1 move up, 2 move right
        int upCount = 0;
        int rightCount = 0;
        while (true) {
            int moved;
            if ((moved = ctx.moveLeftWithin(jumpMovingPathLimit, extraEnsure)) != -1) {
                lastX = ctx.getX();
                state = 0;
                upCount = 0;
                rightCount = 0;
                continue;
            }
            if ((moved = ctx.moveUpWithin(jumpMovingPathLimit - upCount, extraEnsure)) != -1) {
                upCount += moved;
                if (upCount > falsePathLimit) {
                    logger.accept("end step 3 enter step 4, too many up move: result = (" + lastX + ", " + lastY + ")");
                    return runStep4();
                }

                lastY = ctx.getY();
                state = 1;
                rightCount = 0;
                continue;
            }
            if (state == 1 || state == 2) {
                if ((moved = ctx.moveRightWithin(jumpMovingPathLimit - rightCount, extraEnsure)) != -1) {
                    state = 2;
                    if (rightCount > falsePathLimit) {
                        logger.accept("finished at step 3, too many right move: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                        return new int[]{lastX, lastY};
                    }

                    rightCount += moved;
                    continue;
                }
            }
            logger.accept("unable to go left, up or right, finished at step 3: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
            return new int[]{lastX, lastY};
        }
    }

    private int[] runStep4() {
        int lastX = ctx.getX();
        int lastY = ctx.getY();
        int state = 0; // 0 move up, 1 move right, 2 move down
        int rightCount = 0;
        int downCount = 0;
        while (true) {
            int moved;
            if ((moved = ctx.moveUpWithin(jumpMovingPathLimit, extraEnsure)) != -1) {
                if (ctx.isCloseToInitial()) {
                    logger.accept("reaches initial point when moving up in step 4");
                    return ctx.getXY();
                }
                lastY = ctx.getY();
                state = 0;
                rightCount = 0;
                downCount = 0;
                continue;
            }
            if ((moved = ctx.moveRightWithin(jumpMovingPathLimit - rightCount, extraEnsure)) != -1) {
                if (ctx.isCloseToInitial()) {
                    logger.accept("reaches initial point when moving right in step 4");
                    return ctx.getXY();
                }
                rightCount += moved;
                if (rightCount > falsePathLimit) {
                    logger.accept("too many right move, finished at step 4: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                    return new int[]{lastX, lastY};
                }

                lastX = ctx.getX();
                state = 1;
                downCount = 0;
                continue;
            }
            if (state == 1 || state == 2) {
                if ((moved = ctx.moveDownWithin(jumpMovingPathLimit - downCount, extraEnsure)) != -1) {
                    downCount += moved;
                    if (downCount > falsePathLimit) {
                        logger.accept("too many up move, finished at step 4: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
                        return new int[]{lastX, lastY};
                    }

                    state = 2;
                    continue;
                }
            }
            logger.accept("unable to go up, right or down, finished at step 4: result = (" + lastX + ", " + lastY + "), final point = (" + ctx.getX() + ", " + ctx.getY() + ")");
            return new int[]{lastX, lastY};
        }
    }
}
