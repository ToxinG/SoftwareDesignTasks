import com.google.gson.JsonObject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import session.NetworkSession;
import session.VKSession;
import statistics.NetworkPostStatistics;
import statistics.VKPostStatistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class HashtagFrequencyTest {
    private final Random RANDOM = new Random();

    @Nested
    public class SessionTest {

        @Nested
        public class VKSessionTest {
            private NetworkSession session = new VKSession("C:\\Users\\anton\\IdeaProjects\\HashtagFrequency\\src\\authorization\\user_actor");

            @Test
            public void connectionTest() {
                if (!session.checkConnection()) {
                    throw new IllegalStateException("No connection");
                }
            }

            @Test
            public void queryTest() {
                try {
                    int hours = RANDOM.nextInt(23) + 1;

                    NetworkPostStatistics stats = session.makeSearchQuery("#xiaomi", hours);
                    assertNotNull(stats);

                    List<Integer> statsByHours = stats.getStatsByHours();
                    for (int i = 0; i < hours; i++) {
                        if (statsByHours.get(i) <= 0) {
                            fail("Non-positive amount");
                        }
                    }

                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                } catch (IllegalStateException ise) {

                }
            }

            @Test
            public void statMockTest() {
                int hours = RANDOM.nextInt(23) + 1;
                List<Integer> expectedResult = new ArrayList<>();
                List<JsonObject> mockResponse = new ArrayList<>();
                int amount, date;
                JsonObject obj;
                for (int i = 0; i < hours; i++) {
                    amount = RANDOM.nextInt(16);
                    expectedResult.add(i, amount);
                    for (int j = 0; j < amount; j++) {
                        date = RANDOM.nextInt(3600) + i * 3600;
                        obj = new JsonObject();
                        obj.addProperty("date", date);
                        mockResponse.add(obj);
                    }
                }
                NetworkPostStatistics stats = new VKPostStatistics("", hours, mockResponse, 0);
                List<Integer> actualResult = stats.getStatsByHours();
                for (int i = 0; i < hours; i++) {
                    assertEquals(expectedResult.get(i), actualResult.get(i));
                }
            }
        }
    }

}
