package com.leonardobishop.quests.bungee;

import com.leonardobishop.quests.bungee.listener.PluginMessageListener;
import com.leonardobishop.quests.bungee.lock.DataLockManager;
import com.leonardobishop.quests.common.enums.PluginMessagingChannels;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeQuestsPlugin extends Plugin {

    private DataLockManager dataLockManager;

    @Override
    public void onEnable() {
        this.dataLockManager = new DataLockManager();

        //TODO: https://github.com/LMBishop/Quests/issues/180

//        super.getProxy().registerChannel(PluginMessagingChannels.QUESTS_LOCKS_CHANNEL);
//        super.getProxy().getPluginManager().registerListener(this, new PluginMessageListener(this));
    }

    public DataLockManager getDataLockManager() {
        return dataLockManager;
    }

}
