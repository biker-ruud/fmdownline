package nl.fm.downline.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ruud de Jong
 */
public class LevelRanges {

    private LevelRanges() {
        // Utility class
    }

    private static final Map<Integer, Range> LEVEL_RANGE_MAP = new HashMap<Integer, Range>();

    static {
        LEVEL_RANGE_MAP.put(3, new Range(0f, 300f));
        LEVEL_RANGE_MAP.put(6, new Range(300f, 1200f));
        LEVEL_RANGE_MAP.put(9, new Range(1200f, 3600f));
        LEVEL_RANGE_MAP.put(12, new Range(3600f, 7200f));
        LEVEL_RANGE_MAP.put(15, new Range(7200f, 12000f));
        LEVEL_RANGE_MAP.put(18, new Range(12000f, 20400f));
        LEVEL_RANGE_MAP.put(21, new Range(20400f, 30000f));
    }

    public static Range getRange(int level) {
        return LEVEL_RANGE_MAP.get(level);
    }

    private static class Range {
        private float min;
        private float max;

        public Range(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }

    }
}
