package statistics;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VKPostStatistics implements NetworkPostStatistics {

    private final String KEY;
    private final int PERIOD;
    private final long START;
    private final List<JsonObject> POSTS;

    public VKPostStatistics(String searchKey, int hours, List<JsonObject> posts, int startTime) {
        if (searchKey == null || hours < 0 || posts == null) {
            String message = "Arguments of " + this.getClass().getSimpleName() +
                    " constructor cannot be null";
            throw new IllegalArgumentException(message);
        }

        this.KEY = searchKey;
        this.PERIOD = hours;
        this.START = startTime;
        this.POSTS = new ArrayList<>(posts);
    }

    @Override
    public List<Integer> getStatsByHours() {
        List<Integer> stats = new ArrayList<>();
        for (int i = 0; i < PERIOD; i++) {
            stats.add(i, 0);
        }
        for (JsonObject post : POSTS) {
            int hour = (int) toHours(post.getAsJsonPrimitive("date").getAsInt() - START);
            stats.set(hour, stats.get(hour) + 1);
        }
        return stats;
    }

    private long toHours(long time) {
        return time / 3600;
    }
}
