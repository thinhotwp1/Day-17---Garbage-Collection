import java.util.ArrayList;
import java.util.List;

public class DemoGC {

    // Lưu object sống lâu → để đẩy chúng sang Old Gen
    private static final List<Object> longLivedObjects = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Start DemoGC — see log in gc.log ...");

        generateShortLivedObjects(100_000);

        promoteToOldGen();

        triggerMixedGC();

        triggerFullGC();

        triggerMetaspaceGC();

        System.out.println("Demo end — check gc.log!");
    }

    private static void generateShortLivedObjects(int count) throws InterruptedException {
        System.out.println("Step 1: Generate short-lived objects (Young GC expected)");
        for (int i = 0; i < count; i++) {
            if (i % 10_000 == 0) {
                System.out.printf("... created %,d short objects%n", i);
                Thread.sleep(50);
            }
        }
    }

    private static void promoteToOldGen() throws InterruptedException {
        System.out.println("Step 2: Promote objects to Old Generation");
        for (int i = 0; i < 10_000; i++) {
            byte[] obj = new byte[1024 * 100]; // 100 KB 1 object
            longLivedObjects.add(obj);
            if (i % 1000 == 0) {
                System.out.printf("... promoted %,d objects to Old Gen%n", i);
                Thread.sleep(100);
            }
        }
    }

    private static void triggerMixedGC() throws InterruptedException {
        System.out.println("Step 3: Trigger Mixed GC (Young + Old)");
        for (int i = 0; i < 100_000; i++) {
            byte[] data = new byte[1024]; // 1KB
            if (i % 10_000 == 0) {
                System.out.printf("... allocated %,d small objects%n", i);
                Thread.sleep(50);
            }
        }
    }

    private static void triggerFullGC() {
        System.out.println("💣 Step 4: Trigger Full GC (OutOfMemory simulation)");
        try {
            List<byte[]> list = new ArrayList<>();
            for (int i = 0; i < 1_000_000; i++) {
                list.add(new byte[1024 * 1024]); // 1MB per object
            }
        } catch (OutOfMemoryError e) {
            System.err.println("OutOfMemoryError triggered (Full GC forced)");
        }
    }

    private static void triggerMetaspaceGC() {
        System.out.println("Step 5: Trigger Metaspace GC (dynamic class loading)");
        try {
            for (int i = 0; i < 10_000; i++) {
                ClassLoader loader = new ClassLoader() {};
                loader.loadClass("java.lang.String");
                if (i % 1000 == 0) {
                    System.out.printf("... loaded %,d classes dynamically%n", i);
                }
            }
        } catch (Throwable e) {
            System.err.println("Metaspace GC triggered or OutOfMemory in Metaspace");
        }
    }
}
