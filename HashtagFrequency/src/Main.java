import session.NetworkSession;
import session.VKSession;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args == null || args.length < 1) {
            String message = "Missing first argument - hashtag to search by";
            throw new IllegalArgumentException(message);
        }

        if (args.length < 2) {
            String message = "Missing second argument - time period in hours";
            throw new IllegalArgumentException(message);
        }

        String hashtag = args [0];

        int hours = 0;

        try {
            hours = Math.abs(Integer.parseInt(args[1]));
        } catch (NumberFormatException e) {
            String message = "Second argument - time period in hours - must be a number";
            throw new IllegalArgumentException(message);
        }

        NetworkSession session = new VKSession("C:\\Users\\anton\\IdeaProjects\\HashtagFrequency\\src\\authorization\\user_actor");
        List<Integer> statistics = (session.makeSearchQuery(hashtag, hours)).getStatsByHours();
        for(int i = 0; i < hours; i++) {
            Integer result = statistics.get(i);
            if (result == null) {
                System.out.print(0 + " ");
            } else {
                System.out.print(result + " ");
            }
        }
    }
}
