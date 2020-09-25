package session;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.client.ClientResponse;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.queries.newsfeed.NewsfeedSearchQuery;
import statistics.NetworkPostStatistics;
import statistics.VKPostStatistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VKSession implements NetworkSession {

    private static UserActor ACTOR;
    private VkApiClient client;

    public VKSession(String path) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path
                /*"C:\\Users\\anton\\IdeaProjects\\HashtagFrequency\\src\\authorization\\user_actor"*/))) {
            int userid = Integer.parseInt(br.readLine());
            String accessToken = br.readLine();
            ACTOR = new UserActor(userid, accessToken);
            TransportClient transportClient = HttpTransportClient.getInstance();
            this.client = new VkApiClient(transportClient);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkConnection() {
        return this.client != null;
    }

    @Override
    public NetworkPostStatistics makeSearchQuery(String searchKey, int hours) throws IOException {

        if (!checkConnection()) {
            throw new IllegalStateException("Not connected");
        }

        try {
            Date date = new Date();
            int endTime = (int) (date.getTime() / 1000);
            int startTime = (int) (date.getTime() / 1000) - hours * 60 * 60;
            List<JsonObject> posts = new ArrayList<>();

            String startFrom = "";
            do {
                NewsfeedSearchQuery query = client.newsfeed().search(ACTOR)
                        .q(searchKey)
                        .count(200)
                        .startTime(startTime)
                        .endTime(endTime)
                        .startFrom(startFrom);

                ClientResponse clientResponse = query.executeAsRaw();
                JsonElement json = new JsonParser().parse(clientResponse.getContent());
                JsonObject response = json.getAsJsonObject().getAsJsonObject("response");
                JsonArray items = response.getAsJsonArray("items");
                items.forEach(i -> posts.add(i.getAsJsonObject()));

                startFrom = "";
                if (response.has("next_from")) {
                    startFrom = response.getAsJsonPrimitive("next_from").getAsString();
                }
            } while (startFrom.length() > 0);
            return new VKPostStatistics(searchKey, hours, posts, startTime);
        } catch (ClientException e) {
            this.client = null;
            throw new IOException(e);
        }
    }
}
