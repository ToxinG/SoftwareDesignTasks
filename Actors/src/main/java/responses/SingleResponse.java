package responses;

public class SingleResponse {
    private String url;
    private String title;

    public SingleResponse(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title + ": " + url;
    }
}
