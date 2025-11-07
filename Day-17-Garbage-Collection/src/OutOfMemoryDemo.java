import java.util.ArrayList;
import java.util.List;

public class OutOfMemoryDemo {

    public static void main(String[] args) {
        List<byte[]> memoryHog = new ArrayList<>();
        int iteration = 0;

        System.out.println("Starting OOM demo...");
        try {
            while (true) {
                // Mỗi vòng cấp phát 10MB
                byte[] block = new byte[10 * 1024 * 1024];
                memoryHog.add(block);
                iteration++;

                if (iteration % 10 == 0) {
                    System.out.println("Allocated ~" + (iteration * 10) + " MB so far");
                    Thread.sleep(500);
                }
            }
        } catch (OutOfMemoryError e) {
            System.err.println("🔥 Caught OOM after allocating ~" + (iteration * 10) + " MB");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
