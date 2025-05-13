package com.leonardobishop.quests.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark classes which have been rewritten to meet project revamp requirements.
 */
@Retention(value = RetentionPolicy.SOURCE)
@Target(value = ElementType.TYPE)
public @interface Modern {

    Type type();

    enum Type {
        FULL,
        PARTIAL
    }
}
