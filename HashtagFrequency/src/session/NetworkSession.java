package session;

import statistics.NetworkPostStatistics;

import java.io.IOException;

public interface NetworkSession {

    /**
     * Checks if network session connection is established.
     *
     *
     * @return<i>true</i> if the service can respond to requests (etc.);
     * 	 	       <i>false</i> otherwise
     */
    boolean checkConnection();

    /**
     * Make a search request to get usage frequency of the specified word in social network posts.
     *
     * The result is returned as an instance of {@link NetworkPostStatistics} that contains all found data for the
     * <i>hours</i> by the <i>searchKey</i>.
     *
     * @param searchKey word that network API is requested to search for posts by.
     * @param hours time period given in hours before the moment when the request is performed that network API
     *              is requested to search for posts by.
     *
     * @return instance of statistics data container
     *
     * @throws IOException
     *
     * @see NetworkPostStatistics
     */
    NetworkPostStatistics makeSearchQuery(String searchKey, int hours) throws IOException;
}
