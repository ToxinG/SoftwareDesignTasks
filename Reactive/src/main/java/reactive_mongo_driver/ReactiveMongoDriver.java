package reactive_mongo_driver;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.Success;
import rx.Observable;

import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class ReactiveMongoDriver {

    public static MongoClient client = createMongoClient();


    public static Success createUser(User user) {
        return client.getDatabase("rxtest").getCollection("user").insertOne(user.getDocument()).timeout(10, TimeUnit.SECONDS).toBlocking().single();
    }

    public static Success createProduct(Product good) {
        return client.getDatabase("rxtest").getCollection("good").insertOne(good.getDocument()).timeout(10, TimeUnit.SECONDS).toBlocking().single();
    }

    public static Observable<String> findUserById(Integer id) {
        return client.getDatabase("rxtest").getCollection("user").find(eq("id", id)).first().map(d -> new User(d).currency);
    }

    public static Observable<String> getAllProducts(Integer id) {
        return findUserById(id)
                .flatMap(cur -> client
                        .getDatabase("rxtest")
                        .getCollection("good")
                        .find()
                .toObservable()
                        .map(d -> new Product(d)
                                .toString(cur))
                        .reduce((str1, str2) -> str1 + ", " + str2));
    }

    private static MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
}