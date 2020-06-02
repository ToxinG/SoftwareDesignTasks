import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        RequestHandler requestHandler = new RequestHandler(new PrintWriter(System.out));
        requestHandler.getResponses(reader.readLine());
    }
}
