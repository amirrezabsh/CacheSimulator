import java.lang.reflect.Array;
import java.util.Scanner;

public class CacheSimulator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // reading first line of input.
        String firstLine = sc.nextLine();
        // splitting first line to extract data for determining the type of cache.
        String[] firstLineSplit = firstLine.split(" - ");
        // reading second line of input.
        String secondLine = sc.nextLine();
        // splitting second line to extract size of the cache(s).
        String[] secondLineSplit = secondLine.split(" - ");
        cacheSettingsPrint(firstLineSplit, secondLineSplit);
    }

    public static void cacheSettingsPrint(String[] firstLineSplit, String[] secondLineSplit) {
        System.out.println("***CACHE SETTINGS***");
        if (firstLineSplit[1].equals("1")) {
            System.out.println("Split I- D-cache");
            System.out.println("I-cache size: " + secondLineSplit[0]);
            System.out.println("D-cache size: " + secondLineSplit[1]);
        } else {
            System.out.println("Unified I- D-cache");
            System.out.println("Size: " + secondLineSplit[0]);
        }
        System.out.println("Associativity: " + firstLineSplit[2]);
        System.out.println("Block size: " + firstLineSplit[0]);
        if (firstLineSplit[3].equals("wb"))
            System.out.println("Write policy: WRITE BACK");
        else
            System.out.println("Write policy: WRITE THROUGH");
        if (firstLineSplit[4].equals("wa"))
            System.out.println("Allocation policy: WRITE ALLOCATE");
        else
            System.out.println("Allocation policy: WRITE NO ALLOCATE");
    }
}
