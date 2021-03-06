import java.util.*;

public class CacheSimulator {
    public static void main(String[] args) {
        int counter = 0;
        Scanner sc = new Scanner(System.in);
        String firstLine = sc.nextLine();
        String[] firstLineSplit = firstLine.split(" - ");
        String secondLine = sc.nextLine();
        String[] secondLineSplit = secondLine.split(" - ");
        String blockSize = firstLineSplit[0];
        int blockSizeInInt = Integer.parseInt(blockSize);
        Boolean splitOrUnified;
        if (firstLineSplit[1].equals("0"))
            splitOrUnified = false;
        else
            splitOrUnified = true;
        String associativity = firstLineSplit[2];
        int associativityInInt = Integer.parseInt(associativity);
        Boolean backOrThrough;
        if (firstLineSplit[3].equals("wb"))
            backOrThrough = true;
        else
            backOrThrough = false;
        Boolean allocateOrNoAllocate;
        if (firstLineSplit[4].equals("wa"))
            allocateOrNoAllocate = true;
        else
            allocateOrNoAllocate = false;
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
        int ICacheSetsCount = 0;
        int DCacheSetsCount = 0;
        int cacheSetsCount = 0;
        if (splitOrUnified) {
            ICacheSetsCount = (ICacheSizeInInt / blockSizeInInt) / associativityInInt;
            DCacheSetsCount = (DCacheSizeInInt / blockSizeInInt) / associativityInInt;
        } else
            cacheSetsCount = (cacheSizeInInt / blockSizeInInt) / associativityInInt;
        ArrayList<Queue<String>> ICache = new ArrayList<>();
        ArrayList<Queue<String>> DCache = new ArrayList<>();
        ArrayList<Queue<String>> cache = new ArrayList<>();
        ArrayList<String> dirtyBlocks = new ArrayList<>();
        ArrayList<String> DDirtyBlocks = new ArrayList<>();
        ArrayList<String> IDirtyBlocks = new ArrayList<>();
        int dataHitCount = 0;
        int dataMissCount = 0;
        int instructionHitCount = 0;
        int instructionMissCount = 0;
        int copiesBack = 0;
        int demandFetch = 0;
        int dataAccesses = 0;
        int instructionAccesses = 0;
        int dataReplace = 0;
        int instructionReplace = 0;
        Details dataDetails = new Details(dataHitCount, dataMissCount, dataAccesses, dataReplace);
        Details instructionDetails = new Details(instructionHitCount, instructionMissCount, instructionAccesses, instructionReplace);
        PublicDetails publicDetails = new PublicDetails(copiesBack, demandFetch);
        if (splitOrUnified) {
            for (int i = 0; i < ICacheSetsCount; i++) {
                ICache.add(new LinkedList<>());
            }
            for (int i = 0; i < DCacheSetsCount; i++) {
                DCache.add(new LinkedList<>());
            }
        } else {
            for (int i = 0; i < cacheSetsCount; i++) {
                cache.add(new LinkedList<>());
            }
        }
        while (true) {
            String dataLine = sc.nextLine();
            if (dataLine.length() <= 1)
                break;
            String[] dataSplit = dataLine.split(" ");
            if (!splitOrUnified) {
                if (dataSplit[0].equals("0")) {
                    counter = read(dataSplit[1], cache, blockSizeInInt, cacheSetsCount, associativityInInt, dirtyBlocks, dataDetails, publicDetails, counter);
                }

                if (dataSplit[0].equals("1") && backOrThrough) {
                    counter = writeBack(dataSplit[1], cache, cacheSetsCount, associativityInInt, allocateOrNoAllocate, blockSizeInInt, dirtyBlocks, dataDetails, publicDetails, counter);
                }
                if (dataSplit[0].equals("1") && !backOrThrough) {
                    counter = writeThrough(dataSplit[1], cache, cacheSetsCount, associativityInInt, allocateOrNoAllocate, blockSizeInInt, dataDetails, publicDetails, counter);
                }
                if (dataSplit[0].equals("2")) {
                    counter = read(dataSplit[1], cache, blockSizeInInt, cacheSetsCount, associativityInInt, dirtyBlocks, instructionDetails, publicDetails, counter);
                }
            }
            if (splitOrUnified) {
                if (dataSplit[0].equals("0")) {
                    counter = read(dataSplit[1], DCache, blockSizeInInt, DCacheSetsCount, associativityInInt, DDirtyBlocks, dataDetails, publicDetails, counter);
                }

                if (dataSplit[0].equals("1") && backOrThrough) {
                    counter = writeBack(dataSplit[1], DCache, DCacheSetsCount, associativityInInt, allocateOrNoAllocate, blockSizeInInt, DDirtyBlocks, dataDetails, publicDetails, counter);
                }
                if (dataSplit[0].equals("1") && !backOrThrough) {
                    counter = writeThrough(dataSplit[1], DCache, DCacheSetsCount, associativityInInt, allocateOrNoAllocate, blockSizeInInt, dataDetails, publicDetails, counter);
                }
                if (dataSplit[0].equals("2")) {
                    counter = read(dataSplit[1], ICache, blockSizeInInt, ICacheSetsCount, associativityInInt, IDirtyBlocks, instructionDetails, publicDetails, counter);
                }
            }
        }
        cacheSettingsPrint(firstLineSplit, secondLineSplit);
        if (!splitOrUnified)
            emptyCache(cache, dirtyBlocks, publicDetails, blockSizeInInt);
        if (splitOrUnified) {
            emptyCache(DCache, DDirtyBlocks, publicDetails, blockSizeInInt);
            emptyCache(ICache, IDirtyBlocks, publicDetails, blockSizeInInt);
        }
        printResult( dataDetails, instructionDetails, publicDetails, blockSizeInInt);
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
        System.out.println();
    }

    public static int read(String dataAddress, ArrayList<Queue<String>> cache, int blockSize, int cacheSetsCount, int associativity, ArrayList<String> dirtyBlocks, Details details, PublicDetails publicDetails, int counter) {
        counter++;
        int addressInInt = Integer.parseInt(dataAddress, 16);
        addressInInt = addressInInt / blockSize;
        dataAddress = "" + addressInInt;
        addressInInt = addressInInt % cacheSetsCount;
        if (cache.get(addressInInt).contains(dataAddress)) {
            Queue<String> tmp = new LinkedList<>();
            while (!cache.get(addressInInt).peek().equals(dataAddress)) {
                tmp.add(cache.get(addressInInt).poll());
            }
            cache.get(addressInInt).remove();
            cache.get(addressInInt).add(dataAddress);
            while (!cache.get(addressInInt).isEmpty()) {
                tmp.add(cache.get(addressInInt).poll());
            }
            while (!tmp.isEmpty())
                cache.get(addressInInt).add(tmp.poll());

            details.setHits(details.getHits() + 1);
        } else {
            details.setMisses(details.getMisses() + 1);
            publicDetails.setDemandFetch(publicDetails.getDemandFetch() + 1);
            cache.get(addressInInt).add(dataAddress);
            if (cache.get(addressInInt).size() > associativity) {
                details.setReplace(details.getReplace() + 1);
                if (dirtyBlocks.contains(cache.get(addressInInt).peek())) {
                    publicDetails.setCopiesBack(publicDetails.getCopiesBack() + (blockSize / 4));
                    dirtyBlocks.remove(cache.get(addressInInt).poll());
                } else
                    cache.get(addressInInt).remove();
            }
        }
        details.setAccesses(details.getAccesses() + 1);
        return counter;
    }

    public static int writeBack(String dataAddress, ArrayList<Queue<String>> cache, int cacheSetsCount, int associativity, Boolean allocateOrNoAllocate, int blockSize, ArrayList<String> dirtyBlocks, Details details, PublicDetails publicDetails, int counter) {
        counter++;
        int addressInInt = Integer.parseInt(dataAddress, 16);
        addressInInt = addressInInt / blockSize;
        dataAddress = "" + addressInInt;
        addressInInt = addressInInt % cacheSetsCount;
        if (cache.get(addressInInt).contains(dataAddress)) {
            if (!dirtyBlocks.contains(dataAddress))
                dirtyBlocks.add(dataAddress);
            details.setHits(details.getHits() + 1);
        }
        if (allocateOrNoAllocate) {
            if (cache.get(addressInInt).contains(dataAddress)) {
                Queue<String> tmp = new LinkedList<>();
                while (!cache.get(addressInInt).peek().equals(dataAddress)) {
                    tmp.add(cache.get(addressInInt).poll());
                }
                cache.get(addressInInt).remove();
                while (!cache.get(addressInInt).isEmpty()) {
                    tmp.add(cache.get(addressInInt).poll());
                }
                while (!tmp.isEmpty())
                    cache.get(addressInInt).add(tmp.poll());

            } else {
                details.setMisses(details.getMisses() + 1);
                publicDetails.setDemandFetch(publicDetails.getDemandFetch() + 1);
                if (!dirtyBlocks.contains(dataAddress))
                    dirtyBlocks.add(dataAddress);
            }
            cache.get(addressInInt).add(dataAddress);
            if (cache.get(addressInInt).size() > associativity) {
                details.setReplace(details.getReplace() + 1);
                if (dirtyBlocks.contains(cache.get(addressInInt).peek())) {
                    publicDetails.setCopiesBack(publicDetails.getCopiesBack() + (blockSize / 4));
                    dirtyBlocks.remove(cache.get(addressInInt).poll());
                } else
                    cache.get(addressInInt).remove();
            }
        } else {
            if (!cache.get(addressInInt).contains(dataAddress)) {
                publicDetails.setCopiesBack(publicDetails.getCopiesBack() + 1);
                details.setMisses(details.getMisses() + 1);
            }
            else {
                Queue<String> tmp = new LinkedList<>();
                while (!cache.get(addressInInt).peek().equals(dataAddress)) {
                    tmp.add(cache.get(addressInInt).poll());
                }
                cache.get(addressInInt).remove();
                cache.get(addressInInt).add(dataAddress);
                while (!cache.get(addressInInt).isEmpty()) {
                    tmp.add(cache.get(addressInInt).poll());
                }
                while (!tmp.isEmpty())
                    cache.get(addressInInt).add(tmp.poll());
            }
        }
        details.setAccesses(details.getAccesses() + 1);
        return counter;
    }

    public static int writeThrough(String dataAddress, ArrayList<Queue<String>> cache, int cacheSetsCount, int associativity, Boolean allocateOrNoAllocate, int blockSize, Details details, PublicDetails publicDetails, int counter) {
        counter++;
        int addressInInt = Integer.parseInt(dataAddress, 16);
        addressInInt = addressInInt / blockSize;
        dataAddress = "" + addressInInt;
        addressInInt = addressInInt % cacheSetsCount;
        if (cache.get(addressInInt).contains(dataAddress)) {
            details.setHits(details.getHits() + 1);
            publicDetails.setCopiesBack(publicDetails.getCopiesBack() + 1);

        }
        if (allocateOrNoAllocate) {
            if (cache.get(addressInInt).contains(dataAddress)) {
                Queue<String> tmp = new LinkedList<>();
                while (!cache.get(addressInInt).peek().equals(dataAddress)) {
                    tmp.add(cache.get(addressInInt).poll());
                }
                cache.get(addressInInt).remove();
                while (!cache.get(addressInInt).isEmpty()) {
                    tmp.add(cache.get(addressInInt).poll());
                }
                while (!tmp.isEmpty())
                    cache.get(addressInInt).add(tmp.poll());

            } else {
                details.setMisses(details.getMisses() + 1);
                publicDetails.setDemandFetch(publicDetails.getDemandFetch() + 1);
                publicDetails.setCopiesBack(publicDetails.getCopiesBack() + 1);
            }
            cache.get(addressInInt).add(dataAddress);
            if (cache.get(addressInInt).size() > associativity) {
                details.setReplace(details.getReplace() + 1);
                cache.get(addressInInt).remove();
            }
        } else {
            if (!cache.get(addressInInt).contains(dataAddress)) {
                publicDetails.setCopiesBack(publicDetails.getCopiesBack() + 1);
                details.setMisses(details.getMisses() + 1);
            }
            else {
                Queue<String> tmp = new LinkedList<>();
                while (!cache.get(addressInInt).peek().equals(dataAddress)) {
                    tmp.add(cache.get(addressInInt).poll());
                }
                cache.get(addressInInt).remove();
                cache.get(addressInInt).add(dataAddress);
                while (!cache.get(addressInInt).isEmpty()) {
                    tmp.add(cache.get(addressInInt).poll());
                }
                while (!tmp.isEmpty())
                    cache.get(addressInInt).add(tmp.poll());
            }
        }
        details.setAccesses(details.getAccesses() + 1);
        return counter;
    }

    public static void emptyCache(ArrayList<Queue<String>> cache, ArrayList<String> dirtyBlocks, PublicDetails publicDetails, int blockSize) {
        publicDetails.setCopiesBack(publicDetails.getCopiesBack() + dirtyBlocks.size() * (blockSize / 4));
    }

    public static void printResult( Details dataDetails, Details
            instructionDetails, PublicDetails publicDetails, int blockSize) {
        System.out.println("***CACHE STATISTICS***");
        System.out.println("INSTRUCTIONS");
        publicDetails.setDemandFetch(publicDetails.getDemandFetch() * (blockSize / 4));
        double instructionMissRate = 0;
        if (instructionDetails.getMisses() == 0)
            instructionMissRate = 0.0000;
        else
            instructionMissRate = (float) instructionDetails.getMisses() / (float) instructionDetails.getAccesses();
        String instructionMissRateString = String.format("%.4f", instructionMissRate);
        double instructionHitRate = 0;
        if (instructionDetails.getHits() == 0)
            instructionHitRate = 0.0000;
        else
            instructionHitRate = 1 - instructionMissRate;
        String instructionHitRateString = String.format("%.4f", instructionHitRate);
        double dataMissRate = 0;
        if (dataDetails.getMisses() == 0)
            dataMissRate = 0.0000;
        else
            dataMissRate = (float) dataDetails.getMisses() / (float) dataDetails.getAccesses();
        String dataMissRateString = String.format("%.4f", dataMissRate);
        double dataHitRate = 0;
        if (dataDetails.getHits() == 0)
            dataHitRate = 0.0000;
        else
            dataHitRate = 1 - dataMissRate;
        String dataHitRateString = String.format("%.4f", dataHitRate);
        System.out.println("accesses: " + instructionDetails.getAccesses());
        System.out.println("misses: " + instructionDetails.getMisses());
        System.out.println("miss rate: " + instructionMissRateString + " (hit rate " + instructionHitRateString + ")");
        System.out.println("replace: " + instructionDetails.getReplace());
        System.out.println("DATA");
        System.out.println("accesses: " + dataDetails.getAccesses());
        System.out.println("misses: " + dataDetails.getMisses());
        System.out.println("miss rate: " + dataMissRateString + " (hit rate " + dataHitRateString + ")");
        System.out.println("replace: " + dataDetails.getReplace());
        System.out.println("TRAFFIC (in words)");
        System.out.println("demand fetch: " + publicDetails.getDemandFetch());
        System.out.print("copies back: " + publicDetails.getCopiesBack());
    }

    static class Details {
        int hits;
        int misses;
        int accesses;
        int replace;

        public Details(int hits, int misses, int accesses, int replace) {
            this.hits = hits;
            this.misses = misses;
            this.accesses = accesses;
            this.replace = replace;
        }

        public void setHits(int hits) {
            this.hits = hits;
        }

        public void setMisses(int misses) {
            this.misses = misses;
        }

        public void setAccesses(int accesses) {
            this.accesses = accesses;
        }


        public void setReplace(int replace) {
            this.replace = replace;
        }

        public int getHits() {
            return hits;
        }

        public int getMisses() {
            return misses;
        }

        public int getAccesses() {
            return accesses;
        }


        public int getReplace() {
            return replace;
        }
    }

    static class PublicDetails {
        int copiesBack;
        int demandFetch;

        public PublicDetails(int copiesBack, int demandFetch) {
            this.copiesBack = copiesBack;
            this.demandFetch = demandFetch;
        }

        public int getCopiesBack() {
            return copiesBack;
        }

        public int getDemandFetch() {
            return demandFetch;
        }

        public void setCopiesBack(int copiesBack) {
            this.copiesBack = copiesBack;
        }

        public void setDemandFetch(int demandFetch) {
            this.demandFetch = demandFetch;
        }
    }
}
