import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WriteThread extends Thread {

    private final SparkServer sparkServer;

    public WriteThread(SparkServer sparkServer) {
        this.sparkServer = sparkServer;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (sparkServer.isAlive()) {
            try {
                String s = br.readLine();
                if (s.startsWith("/")) {
                    switch (s) {
                        case "/stop":
                            sparkServer.shutdown();
                            break;
                        case "/kickall":
                            sparkServer.kickAll(null);
                            break;
                        default:
                            SparkServer.print("Command not found!");
                    }
                } else {
                    sparkServer.broadcastMessage(s, "general", "Server");
                }
            } catch (IOException e) {
                SparkServer.printError(e, "Error reading from console");
            }
        }
    }
}
