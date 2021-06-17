package com.leonardobishop.quests.common.config;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface QuestsLoader {

    Map<String, List<ConfigProblem>> loadQuests(File root);

}
