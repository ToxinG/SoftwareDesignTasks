package statistics;

import java.util.List;

public interface NetworkPostStatistics {

    /**
     * Gets how many times the search key was used during each hour of the specified period by raw API response.
     *
     * @return map where keys are hours and values are usage amounts.
     */
    List<Integer> getStatsByHours();
}
