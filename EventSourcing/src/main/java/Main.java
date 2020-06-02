import reactive_mongo_driver.ReactiveMongoDriver;
import servers.ManagerServer;
import servers.ReportServer;
import servers.EntranceServer;
import storage.EventStorage;

public class Main {
    private final static String DATABASE_NAME = "fitness-center";

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    public void run() {
        EventStorage eventStorage = new EventStorage();
        EntranceServer turnstileServerServer = new EntranceServer(new ReactiveMongoDriver(DATABASE_NAME, eventStorage));
        ManagerServer managerServer = new ManagerServer(new ReactiveMongoDriver(DATABASE_NAME, eventStorage));
        ReportServer reportServer = new ReportServer(new ReactiveMongoDriver(DATABASE_NAME, eventStorage), eventStorage);

        new Thread(new ManagerServerRunner(managerServer)).start();
        new Thread(new EntranceServerRunner(turnstileServerServer)).start();
        new Thread(new ReportServerRunner(reportServer)).start();
    }

    public static class EntranceServerRunner implements Runnable {

        private final EntranceServer entryServer;

        public EntranceServerRunner(EntranceServer entryServer) {
            this.entryServer = entryServer;
        }

        @Override
        public void run() {
            entryServer.run();
        }
    }

    public static class ManagerServerRunner implements Runnable {

        private final ManagerServer managerServer;

        public ManagerServerRunner(ManagerServer managerServer) {
            this.managerServer = managerServer;
        }

        @Override
        public void run() {
            managerServer.run();
        }
    }

    public static class ReportServerRunner implements Runnable {

        private final ReportServer reportServer;

        public ReportServerRunner(ReportServer reportServer) {
            this.reportServer = reportServer;
        }

        @Override
        public void run() {
            reportServer.run();
        }
    }
}