package com.leonardobishop.quests.common.util;

public class Format {

    //TODO reimplement customisation
    public static String formatTime(long sec) {
        long hours = sec / 3600;
        long minutes = (sec % 3600) / 60;
        long seconds = ((sec % 3600) % 60) % 60;

//        return Messages.TIME_FORMAT.getMessage()
//                .replace("{hours}", String.format("%02d", hours))
//                .replace("{minutes}", String.format("%02d", minutes))
//                .replace("{seconds}", String.format("%02d", seconds));
        return "{houes}h {minutes}m {seconds}s"
                .replace("{hours}", String.format("%02d", hours))
                .replace("{minutes}", String.format("%02d", minutes))
                .replace("{seconds}", String.format("%02d", seconds));
    }

}
