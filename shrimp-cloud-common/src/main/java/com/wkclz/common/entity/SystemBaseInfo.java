package com.wkclz.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class SystemBaseInfo implements Serializable {

    private List<Disk> disks;
    private ClassLoading classLoading;
    private Compilation compilation;
    private OperatingSystem operatingSystem;
    private PlatformMBeanServer platformMBeanServer;
    private Runtime runtime;
    private Thread thread;
    private Memory memory;
    private List<MemoryManager> memoryManagers;
    private List<GarbageCollector> garbageCollectors;
    private List<MemoryPool> memoryPools;

    @Data
    public static class Disk {
        private String partition;
        private Long totalSpace;
        private Long freeSpace;
        private Long usedSpace;
    }

    @Data
    public static class ClassLoading {
        private Integer loadedClassCount;
        private Long totalLoadedClassCount;
        private Long unloadedClassCount;
    }

    @Data
    public static class Compilation {
        private Long totalCompilationTime;
        private String name;
    }

    @Data
    public static class OperatingSystem {
        private String name;
        private String arch;
        private Integer availableProcessors;
        private BigDecimal systemLoadAverage;
        private String version;

        private Long committedVirtualMemorySize;
        private BigDecimal processCpuLoad;
        private Long processCpuTime;
        private Long totalMemorySize;
        private Long freeMemorySize;
        private Long totalSwapSpaceSize;
        private Long freeSwapSpaceSize;
    }

    @Data
    public static class PlatformMBeanServer {
        private Integer mBeanCount;
        private String defaultDomain;
        private List<String> domains;
    }

    @Data
    public static class Runtime {

        private String name;
        private List<String> inputArguments;
        private String classPath;
        private String libraryPath;
        private String managementSpecVersion;

        private Boolean bootClassPathSupported;
        private String bootClassPath;

        private Long startTime;
        private Long uptime;

        private String specName;
        private String specVendor;
        private String specVersion;

        private String vmName;
        private String vmVendor;
        private String vmVersion;

        private Map<String, String> systemProperties;
    }

    @Data
    public static class Thread {
        private Integer threadCount;
        private long[] allThreadIds;
        private ThreadInfo[] threadInfos;

        private Integer daemonThreadCount;
        private Integer peakThreadCount;
        private Long totalStartedThreadCount;

        private Long currentThreadCpuTime;
        private Long currentThreadUserTime;
    }

    @Data
    public static class Memory {
        private MemoryUsage heapMemoryUsage;
        private MemoryUsage nonHeapMemoryUsage;
        private Integer objectPendingFinalizationCount;
    }

    @Data
    public static class MemoryManager {
        private String name;
        private List<String> memoryPoolNames;
    }

    @Data
    public static class GarbageCollector {
        private Long collectionCount;
        private String name;
        private List<String> memoryPoolNames;
        private Long collectionTime;
    }

    @Data
    public static class MemoryPool {
        private String name;
        private Boolean valid;
        private MemoryType type;
        private MemoryUsage usage;
        private MemoryUsage peakUsage;
        private MemoryUsage collectionUsage;
        private List<String> memoryManagerNames;

        private Long usageThreshold;
        private Long usageThresholdCount;
        private Boolean usageThresholdSupported;
        private Boolean usageThresholdExceeded;

        private Long collectionUsageThreshold;
        private Long collectionUsageThresholdCount;
        private Boolean collectionUsageThresholdExceeded;
        private Boolean collectionUsageThresholdSupported;
    }

}
