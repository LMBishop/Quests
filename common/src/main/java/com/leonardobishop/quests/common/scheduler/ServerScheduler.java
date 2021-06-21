package com.leonardobishop.quests.common.scheduler;

/**
 * The server scheduler wraps the platforms scheduler to allow for use in abstract code.
 */
public interface ServerScheduler {

    void doSync(Runnable runnable);
    void doAsync(Runnable runnable);

}
