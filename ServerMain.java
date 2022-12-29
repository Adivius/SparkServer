public class ServerMain {

    public static void main(String[] args) {
        try {
            new SparkServer(Integer.parseInt(args[0])).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
