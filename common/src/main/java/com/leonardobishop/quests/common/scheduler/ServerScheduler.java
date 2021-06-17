package com.leonardobishop.quests.common.scheduler;

public interface ServerScheduler {

    void doSync(Runnable runnable);
    void doAsync(Runnable runnable);

}
