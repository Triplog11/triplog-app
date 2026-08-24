package triplog.backend.achievement.service;

/**
 * DB에 저장된 target, operator, value 조건을 공통으로 판정합니다.
 */
public final class AchievementConditionEvaluator {

    /** 인스턴스 생성을 막습니다. */
    private AchievementConditionEvaluator() {
    }

    /**
     * 현재 활동 지표가 지정한 조건을 충족하는지 확인합니다.
     */
    public static boolean isSatisfied(
            AchievementContext context,
            String target,
            String operator,
            Integer requiredValue
    ) {
        if (requiredValue == null) {
            return false;
        }
        long currentValue = context.metric(target);
        return switch (operator) {
            case ">=" -> currentValue >= requiredValue;
            case ">" -> currentValue > requiredValue;
            case "=" -> currentValue == requiredValue;
            case "<=" -> currentValue <= requiredValue;
            case "<" -> currentValue < requiredValue;
            default -> throw new IllegalStateException(
                    "Unsupported achievement operator: " + operator
            );
        };
    }
}
