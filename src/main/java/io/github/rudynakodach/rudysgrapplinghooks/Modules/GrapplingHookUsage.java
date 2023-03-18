package io.github.rudynakodach.rudysgrapplinghooks.Modules;

public class GrapplingHookUsage {
    private final int delay;
    private final long useTime;

    public GrapplingHookUsage(int delay, long useTime) {
        this.delay = delay;
        this.useTime = useTime;
    }

    public int getDelay() {
        return delay;
    }

    public long getUseTime() {
        return useTime;
    }
}
