import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
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
        // analyzing cache characteristics
        // block size extraction.
        String blockSize = firstLineSplit[0];
        int blockSizeInInt = Integer.parseInt(blockSize);
        // determining if the cache split or unified
        Boolean splitOrUnified;
        if (firstLineSplit[0].equals("0"))
            splitOrUnified = false;
        else
            splitOrUnified = true;
        // associativity extraction.
        String associativity = firstLineSplit[2];
        // determining if write policy is write-back or write-through.
        Boolean backOrThrough;
        if (firstLineSplit[3].equals("wb"))
            backOrThrough = true;
        else
            backOrThrough = false;
        // determining if allocation policy is write-allocation or no-write-allocation.
        Boolean allocateOrNoAllocate;
        if (firstLineSplit[4].equals("wa"))
            allocateOrNoAllocate = true;
        else
            allocateOrNoAllocate = false;
        // cache size extraction.
        String ICacheSize;
        int ICacheSizeInInt;
        String DCacheSize;
        int DCacheSizeInInt;
        String cacheSize;
        int cacheSizeInInt;
        if (splitOrUnified) {
            ICacheSize = secondLineSplit[0];
            ICacheSizeInInt = Integer.parseInt(ICacheSize);
            DCacheSize = secondLineSplit[1];
            DCacheSizeInInt = Integer.parseInt(DCacheSize);
        } else {
            cacheSize = secondLineSplit[0];
            cacheSizeInInt = Integer.parseInt(cacheSize);
        }
        // extracting load and store instructions
        while (true) {
            String dataLine = sc.nextLine();
            // check when we reach the second '\n'
            if (dataLine.length() <= 1)
                break;
            // ignoring extra part of the line
            dataLine = dataLine.substring(0, 7);
            // splitting the line to read/store value and address value
            String[] dataSplit = dataLine.split(" ");
            // converting address string in hexadecimal to decimal
            int addressInInt = Integer.parseInt(dataSplit[1], 16);
        }
        // printing cache settings based on extracted data from first line and the second one.
        cacheSettingsPrint(firstLineSplit, secondLineSplit);
    }

    public static void cacheSettingsPrint(String[] firstLineSplit, String[] secondLineSplit) {
        // printing title
        System.out.println("***CACHE SETTINGS***");
        // determining if the cache is Harvard or Von Neumann
        if (firstLineSplit[1].equals("1")) {
            // printing Harvard cache characteristics
            System.out.println("Split I- D-cache");
            System.out.println("I-cache size: " + secondLineSplit[0]);
            System.out.println("D-cache size: " + secondLineSplit[1]);
        } else {
            //printing Von Neumann characteristics
            System.out.println("Unified I- D-cache");
            System.out.println("Size: " + secondLineSplit[0]);
        }
        // printing associativity
        System.out.println("Associativity: " + firstLineSplit[2]);
        // printing block size
        System.out.println("Block size: " + firstLineSplit[0]);
        // determining if the cache write policy is write-back or write-through
        if (firstLineSplit[3].equals("wb"))
            System.out.println("Write policy: WRITE BACK");
        else
            System.out.println("Write policy: WRITE THROUGH");
        // determining if the cache write allocation is write-allocate or no-write-allocate
        if (firstLineSplit[4].equals("wa"))
            System.out.println("Allocation policy: WRITE ALLOCATE");
        else
            System.out.println("Allocation policy: WRITE NO ALLOCATE");
    }
}
