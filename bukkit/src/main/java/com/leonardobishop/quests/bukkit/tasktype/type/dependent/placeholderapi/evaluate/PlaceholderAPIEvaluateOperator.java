package com.leonardobishop.quests.bukkit.tasktype.type.dependent.placeholderapi.evaluate;

public enum PlaceholderAPIEvaluateOperator {
    GREATER_THAN {
        @Override
        public boolean compare(double evaluated, double evaluates) {
            return evaluated > evaluates;
        }
    },
    LESS_THAN {
        @Override
        public boolean compare(double evaluated, double evaluates) {
            return evaluated < evaluates;
        }
    },
    GREATER_THAN_OR_EQUAL_TO {
        @Override
        public boolean compare(double evaluated, double evaluates) {
            return evaluated >= evaluates;
        }
    },
    LESS_THAN_OR_EQUAL_TO {
        @Override
        public boolean compare(double evaluated, double evaluates) {
            return evaluated <= evaluates;
        }
    };

    public abstract boolean compare(double evaluated, double evaluates);
}
