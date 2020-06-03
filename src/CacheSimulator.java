import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;

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
        if (firstLineSplit[1].equals("0"))
            splitOrUnified = false;
        else
            splitOrUnified = true;
        // associativity extraction.
        String associativity = firstLineSplit[2];
        int associativityInInt = Integer.parseInt(associativity);
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
        int ICacheSizeInInt = 0;
        String DCacheSize;
        int DCacheSizeInInt = 0;
        String cacheSize;
        int cacheSizeInInt = 0;
        if (splitOrUnified) {
            ICacheSize = secondLineSplit[0];
            ICacheSizeInInt = Integer.parseInt(ICacheSize);
            DCacheSize = secondLineSplit[1];
            DCacheSizeInInt = Integer.parseInt(DCacheSize);
        } else {
            cacheSize = secondLineSplit[0];
            cacheSizeInInt = Integer.parseInt(cacheSize);
        }
        // sets count calculation
        int ICacheSetsCount = 0;
        int DCacheSetsCount = 0;
        int cacheSetsCount = 0;
        if (splitOrUnified) {
            ICacheSetsCount = (ICacheSizeInInt / blockSizeInInt) / associativityInInt;
            DCacheSetsCount = (DCacheSizeInInt / blockSizeInInt) / associativityInInt;
        } else
            cacheSetsCount = (cacheSizeInInt / blockSizeInInt) / associativityInInt;
        // creating cache.
        ArrayList<Queue<String>> ICache = new ArrayList<>(ICacheSetsCount);
        ArrayList<Queue<String>> DCache = new ArrayList<>(DCacheSetsCount);
        ArrayList<Queue<String>> cache = new ArrayList<>(cacheSetsCount);
        ArrayList<String> dirtyBlocks = new ArrayList<>();
        int dataHitCount = 0;
        int dataMissCount = 0;
        int instructionHitCount = 0;
        int instructionMissCount = 0;
        int copiesBack = 0;
        int demandFetch = 0;
        int dataAccesses = 0;
        int instructionAccesses = 0;
        int dataReplace=0;
        int instructionReplace=0;
        if (splitOrUnified) {
            for (int i = 0; i < ICacheSetsCount; i++) {
                ICache.add(new PriorityQueue<>());
            }
            for (int i = 0; i < DCacheSetsCount; i++) {
                DCache.add(new PriorityQueue<>());
            }
        } else {
            for (int i = 0; i < cacheSetsCount; i++) {
                cache.add(new PriorityQueue<>());
            }
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
            if (dataSplit[0].equals("0") && !splitOrUnified) {
                read(dataSplit[1], cache, blockSizeInInt, cacheSetsCount, dataHitCount, dataMissCount, associativityInInt, demandFetch, dirtyBlocks, copiesBack);
                dataAccesses++;
            }

            if (dataSplit[0].equals("1") && backOrThrough) {
                writeBack(dataSplit[1], cache, cacheSetsCount, dataHitCount, dataMissCount, associativityInInt, allocateOrNoAllocate, blockSizeInInt, dirtyBlocks, copiesBack, demandFetch);
                dataAccesses++;
            }
            if (dataSplit[0].equals("2") && !splitOrUnified) {
                read(dataSplit[1], cache, blockSizeInInt, cacheSetsCount, instructionHitCount, instructionMissCount, associativityInInt, demandFetch, dirtyBlocks, copiesBack);
                instructionAccesses++;
            }
        }
        // printing cache settings based on extracted data from first line and the second one.
        cacheSettingsPrint(firstLineSplit, secondLineSplit);
        emptyCache(cache, dirtyBlocks, copiesBack);
        printResult(copiesBack, demandFetch, dataHitCount, dataMissCount, splitOrUnified, dataAccesses, instructionHitCount, instructionMissCount,instructionAccesses,dataReplace,instructionReplace);
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

    public static void read(String dataAddress, ArrayList<Queue<String>> cache, int blockSize, int cacheSetsCount, int hitCount, int missCount, int Associativity, int demandFetch, ArrayList<String> dirtyBlocks, int copiesBack) {
        // converting address string in hexadecimal to decimal
        int addressInInt = Integer.parseInt(dataAddress, 16);
        addressInInt = addressInInt / blockSize;
        addressInInt = addressInInt % cacheSetsCount;

        if (cache.get(addressInInt).contains(dataAddress))
            hitCount++;

        else {
            missCount++;
            demandFetch++;
        }
        cache.get(addressInInt).add(dataAddress);
        if (cache.get(addressInInt).size() > Associativity)
            if (dirtyBlocks.contains(cache.get(addressInInt).peek())) {
                dirtyBlocks.remove(cache.get(addressInInt).peek());
                copiesBack += 4;
            }
        cache.get(addressInInt).remove();
    }

    public static void writeBack(String dataAddress, ArrayList<Queue<String>> cache, int cacheSetsCount, int hitCount, int missCount, int associativity, Boolean allocateOrNoAllocate, int blockSize, ArrayList<String> dirtyBlocks, int copiesBack, int demandFetch) {
        int addressInInt = Integer.parseInt(dataAddress, 16);
        addressInInt = addressInInt / blockSize;
        addressInInt = addressInInt % cacheSetsCount;
        try {
            if (cache.get(addressInInt).contains(dataAddress)) {
                if (!dirtyBlocks.contains(dataAddress))
                    dirtyBlocks.add(dataAddress);
            }
        } catch (NullPointerException e) {
            if (allocateOrNoAllocate) {
                cache.get(addressInInt).add(dataAddress);
                if (cache.get(addressInInt).size() > associativity)
                    cache.get(addressInInt).remove();
                copiesBack++;
                demandFetch++;
            } else {
                copiesBack++;
            }
        }
    }

    public static void emptyCache(ArrayList<Queue<String>> cache, ArrayList<String> dirtyBlocks, int copiesBack) {
        for (int i = 0; i < cache.size(); i++) {
            while (cache.get(i).size() != 0) {
                if (dirtyBlocks.contains(cache.get(i).peek())) {
                    dirtyBlocks.remove(cache.get(i).poll());
                    copiesBack += 4;
                }
            }
        }
    }

    public static void printResult(int copiesBack, int demandFetch, int dataHitCount, int dataMissCount, Boolean splitOrUnified, int dataAccesses, int instructionHitCount, int instructionMissCount, int instructionAccesses, int dataReplace, int instructionReplace) {
        System.out.println("***CACHE STATISTICS***");
        System.out.println("INSTRUCTIONS");
        float instructionMissRate = instructionMissCount / (instructionMissCount + instructionHitCount);
        float instructionHitRate = 1 - instructionMissRate;
        float dataMissRate = dataMissCount / (dataMissCount + dataHitCount);
        float dataHitRate = 1 - dataMissRate;
        if (!splitOrUnified) {
            System.out.println("accesses: " + instructionAccesses);
            System.out.println("misses: " + instructionMissCount);
            System.out.println("miss rate: " + instructionMissRate + " (hit rate " + instructionHitRate + ")");
            System.out.println("replace: " + instructionReplace);
            System.out.println("DATA");
            System.out.println("accesses: " + dataAccesses);
            System.out.println("misses: " + dataMissCount);
            System.out.println("miss rate: " + dataMissRate + " (hit rate " + dataHitRate + ")");
            System.out.println("replace: " + dataReplace);
            System.out.println("TRAFFIC (in words)");
            System.out.println("demand fetch: " + demandFetch);
            System.out.println("copies back: " + copiesBack);
        }
    }
}
