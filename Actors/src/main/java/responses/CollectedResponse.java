package responses;

import java.util.List;

public class CollectedResponse {
    private String name;
    private List<SingleResponse> responses;

    public CollectedResponse(String name, List<SingleResponse> responses) {
        this.name = name;
        this.responses = responses;
    }

    public String getName() {
        return name;
    }

    public List<SingleResponse> getResponses() {
        return responses;
    }
}