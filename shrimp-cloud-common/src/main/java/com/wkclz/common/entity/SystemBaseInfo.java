package com.wkclz.common.entity;

import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SystemBaseInfo {

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

    public ClassLoading getClassLoading() {
        return classLoading;
    }

    public void setClassLoading(ClassLoading classLoading) {
        this.classLoading = classLoading;
    }

    public Compilation getCompilation() {
        return compilation;
    }

    public void setCompilation(Compilation compilation) {
        this.compilation = compilation;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public PlatformMBeanServer getPlatformMBeanServer() {
        return platformMBeanServer;
    }

    public void setPlatformMBeanServer(PlatformMBeanServer platformMBeanServer) {
        this.platformMBeanServer = platformMBeanServer;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public List<MemoryManager> getMemoryManagers() {
        return memoryManagers;
    }

    public void setMemoryManagers(List<MemoryManager> memoryManagers) {
        this.memoryManagers = memoryManagers;
    }

    public List<GarbageCollector> getGarbageCollectors() {
        return garbageCollectors;
    }

    public void setGarbageCollectors(List<GarbageCollector> garbageCollectors) {
        this.garbageCollectors = garbageCollectors;
    }

    public List<MemoryPool> getMemoryPools() {
        return memoryPools;
    }

    public void setMemoryPools(List<MemoryPool> memoryPools) {
        this.memoryPools = memoryPools;
    }

    public List<Disk> getDisks() {
        return disks;
    }

    public void setDisks(List<Disk> disks) {
        this.disks = disks;
    }

    public static class Disk {
        private String partition;
        private Long totalSpace;
        private Long freeSpace;
        private Long usedSpace;

        public String getPartition() {
            return partition;
        }

        public void setPartition(String partition) {
            this.partition = partition;
        }

        public Long getTotalSpace() {
            return totalSpace;
        }

        public void setTotalSpace(Long totalSpace) {
            this.totalSpace = totalSpace;
        }

        public Long getFreeSpace() {
            return freeSpace;
        }

        public void setFreeSpace(Long freeSpace) {
            this.freeSpace = freeSpace;
        }

        public Long getUsedSpace() {
            return usedSpace;
        }

        public void setUsedSpace(Long usedSpace) {
            this.usedSpace = usedSpace;
        }
    }

    public static class ClassLoading {
        private Integer loadedClassCount;
        private Long totalLoadedClassCount;
        private Long unloadedClassCount;

        public Integer getLoadedClassCount() {
            return loadedClassCount;
        }

        public void setLoadedClassCount(Integer loadedClassCount) {
            this.loadedClassCount = loadedClassCount;
        }

        public Long getTotalLoadedClassCount() {
            return totalLoadedClassCount;
        }

        public void setTotalLoadedClassCount(Long totalLoadedClassCount) {
            this.totalLoadedClassCount = totalLoadedClassCount;
        }

        public Long getUnloadedClassCount() {
            return unloadedClassCount;
        }

        public void setUnloadedClassCount(Long unloadedClassCount) {
            this.unloadedClassCount = unloadedClassCount;
        }
    }

    public static class Compilation {
        private Long totalCompilationTime;
        private String name;

        public Long getTotalCompilationTime() {
            return totalCompilationTime;
        }

        public void setTotalCompilationTime(Long totalCompilationTime) {
            this.totalCompilationTime = totalCompilationTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArch() {
            return arch;
        }

        public void setArch(String arch) {
            this.arch = arch;
        }

        public Integer getAvailableProcessors() {
            return availableProcessors;
        }

        public void setAvailableProcessors(Integer availableProcessors) {
            this.availableProcessors = availableProcessors;
        }

        public BigDecimal getSystemLoadAverage() {
            return systemLoadAverage;
        }

        public void setSystemLoadAverage(BigDecimal systemLoadAverage) {
            this.systemLoadAverage = systemLoadAverage;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Long getCommittedVirtualMemorySize() {
            return committedVirtualMemorySize;
        }

        public void setCommittedVirtualMemorySize(Long committedVirtualMemorySize) {
            this.committedVirtualMemorySize = committedVirtualMemorySize;
        }

        public BigDecimal getProcessCpuLoad() {
            return processCpuLoad;
        }

        public void setProcessCpuLoad(BigDecimal processCpuLoad) {
            this.processCpuLoad = processCpuLoad;
        }

        public Long getProcessCpuTime() {
            return processCpuTime;
        }

        public void setProcessCpuTime(Long processCpuTime) {
            this.processCpuTime = processCpuTime;
        }

        public Long getTotalMemorySize() {
            return totalMemorySize;
        }

        public void setTotalMemorySize(Long totalMemorySize) {
            this.totalMemorySize = totalMemorySize;
        }

        public Long getFreeMemorySize() {
            return freeMemorySize;
        }

        public void setFreeMemorySize(Long freeMemorySize) {
            this.freeMemorySize = freeMemorySize;
        }

        public Long getTotalSwapSpaceSize() {
            return totalSwapSpaceSize;
        }

        public void setTotalSwapSpaceSize(Long totalSwapSpaceSize) {
            this.totalSwapSpaceSize = totalSwapSpaceSize;
        }

        public Long getFreeSwapSpaceSize() {
            return freeSwapSpaceSize;
        }

        public void setFreeSwapSpaceSize(Long freeSwapSpaceSize) {
            this.freeSwapSpaceSize = freeSwapSpaceSize;
        }
    }

    public static class PlatformMBeanServer {
        private Integer mBeanCount;
        private String defaultDomain;
        private List<String> domains;

        public Integer getMBeanCount() {
            return mBeanCount;
        }

        public void setMBeanCount(Integer mBeanCount) {
            this.mBeanCount = mBeanCount;
        }

        public String getDefaultDomain() {
            return defaultDomain;
        }

        public void setDefaultDomain(String defaultDomain) {
            this.defaultDomain = defaultDomain;
        }

        public List<String> getDomains() {
            return domains;
        }

        public void setDomains(List<String> domains) {
            this.domains = domains;
        }
    }

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getInputArguments() {
            return inputArguments;
        }

        public void setInputArguments(List<String> inputArguments) {
            this.inputArguments = inputArguments;
        }

        public String getClassPath() {
            return classPath;
        }

        public void setClassPath(String classPath) {
            this.classPath = classPath;
        }

        public String getLibraryPath() {
            return libraryPath;
        }

        public void setLibraryPath(String libraryPath) {
            this.libraryPath = libraryPath;
        }

        public String getManagementSpecVersion() {
            return managementSpecVersion;
        }

        public void setManagementSpecVersion(String managementSpecVersion) {
            this.managementSpecVersion = managementSpecVersion;
        }

        public Boolean getBootClassPathSupported() {
            return bootClassPathSupported;
        }

        public void setBootClassPathSupported(Boolean bootClassPathSupported) {
            this.bootClassPathSupported = bootClassPathSupported;
        }

        public String getBootClassPath() {
            return bootClassPath;
        }

        public void setBootClassPath(String bootClassPath) {
            this.bootClassPath = bootClassPath;
        }

        public Long getStartTime() {
            return startTime;
        }

        public void setStartTime(Long startTime) {
            this.startTime = startTime;
        }

        public Long getUptime() {
            return uptime;
        }

        public void setUptime(Long uptime) {
            this.uptime = uptime;
        }

        public String getSpecName() {
            return specName;
        }

        public void setSpecName(String specName) {
            this.specName = specName;
        }

        public String getSpecVendor() {
            return specVendor;
        }

        public void setSpecVendor(String specVendor) {
            this.specVendor = specVendor;
        }

        public String getSpecVersion() {
            return specVersion;
        }

        public void setSpecVersion(String specVersion) {
            this.specVersion = specVersion;
        }

        public String getVmName() {
            return vmName;
        }

        public void setVmName(String vmName) {
            this.vmName = vmName;
        }

        public String getVmVendor() {
            return vmVendor;
        }

        public void setVmVendor(String vmVendor) {
            this.vmVendor = vmVendor;
        }

        public String getVmVersion() {
            return vmVersion;
        }

        public void setVmVersion(String vmVersion) {
            this.vmVersion = vmVersion;
        }

        public Map<String, String> getSystemProperties() {
            return systemProperties;
        }

        public void setSystemProperties(Map<String, String> systemProperties) {
            this.systemProperties = systemProperties;
        }
    }

    public static class Thread {

        private Integer threadCount;
        private long[] allThreadIds;
        private ThreadInfo[] threadInfos;

        private Integer daemonThreadCount;
        private Integer peakThreadCount;
        private Long totalStartedThreadCount;

        private Long currentThreadCpuTime;
        private Long currentThreadUserTime;

        public Integer getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(Integer threadCount) {
            this.threadCount = threadCount;
        }

        public long[] getAllThreadIds() {
            return allThreadIds;
        }

        public void setAllThreadIds(long[] allThreadIds) {
            this.allThreadIds = allThreadIds;
        }

        public ThreadInfo[] getThreadInfos() {
            return threadInfos;
        }

        public void setThreadInfos(ThreadInfo[] threadInfos) {
            this.threadInfos = threadInfos;
        }

        public Integer getDaemonThreadCount() {
            return daemonThreadCount;
        }

        public void setDaemonThreadCount(Integer daemonThreadCount) {
            this.daemonThreadCount = daemonThreadCount;
        }

        public Integer getPeakThreadCount() {
            return peakThreadCount;
        }

        public void setPeakThreadCount(Integer peakThreadCount) {
            this.peakThreadCount = peakThreadCount;
        }

        public Long getTotalStartedThreadCount() {
            return totalStartedThreadCount;
        }

        public void setTotalStartedThreadCount(Long totalStartedThreadCount) {
            this.totalStartedThreadCount = totalStartedThreadCount;
        }

        public Long getCurrentThreadCpuTime() {
            return currentThreadCpuTime;
        }

        public void setCurrentThreadCpuTime(Long currentThreadCpuTime) {
            this.currentThreadCpuTime = currentThreadCpuTime;
        }

        public Long getCurrentThreadUserTime() {
            return currentThreadUserTime;
        }

        public void setCurrentThreadUserTime(Long currentThreadUserTime) {
            this.currentThreadUserTime = currentThreadUserTime;
        }
    }

    public static class Memory {
        private MemoryUsage heapMemoryUsage;
        private MemoryUsage nonHeapMemoryUsage;
        private Integer objectPendingFinalizationCount;

        public MemoryUsage getHeapMemoryUsage() {
            return heapMemoryUsage;
        }

        public void setHeapMemoryUsage(MemoryUsage heapMemoryUsage) {
            this.heapMemoryUsage = heapMemoryUsage;
        }

        public MemoryUsage getNonHeapMemoryUsage() {
            return nonHeapMemoryUsage;
        }

        public void setNonHeapMemoryUsage(MemoryUsage nonHeapMemoryUsage) {
            this.nonHeapMemoryUsage = nonHeapMemoryUsage;
        }

        public Integer getObjectPendingFinalizationCount() {
            return objectPendingFinalizationCount;
        }

        public void setObjectPendingFinalizationCount(Integer objectPendingFinalizationCount) {
            this.objectPendingFinalizationCount = objectPendingFinalizationCount;
        }
    }

    public static class MemoryManager {
        private String name;
        private List<String> memoryPoolNames;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getMemoryPoolNames() {
            return memoryPoolNames;
        }

        public void setMemoryPoolNames(List<String> memoryPoolNames) {
            this.memoryPoolNames = memoryPoolNames;
        }
    }

    public static class GarbageCollector {
        private Long collectionCount;
        private String name;
        private List<String> memoryPoolNames;
        private Long collectionTime;

        public Long getCollectionCount() {
            return collectionCount;
        }

        public void setCollectionCount(Long collectionCount) {
            this.collectionCount = collectionCount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getMemoryPoolNames() {
            return memoryPoolNames;
        }

        public void setMemoryPoolNames(List<String> memoryPoolNames) {
            this.memoryPoolNames = memoryPoolNames;
        }

        public Long getCollectionTime() {
            return collectionTime;
        }

        public void setCollectionTime(Long collectionTime) {
            this.collectionTime = collectionTime;
        }
    }

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getValid() {
            return valid;
        }

        public void setValid(Boolean valid) {
            this.valid = valid;
        }

        public MemoryType getType() {
            return type;
        }

        public void setType(MemoryType type) {
            this.type = type;
        }

        public MemoryUsage getUsage() {
            return usage;
        }

        public void setUsage(MemoryUsage usage) {
            this.usage = usage;
        }

        public MemoryUsage getPeakUsage() {
            return peakUsage;
        }

        public void setPeakUsage(MemoryUsage peakUsage) {
            this.peakUsage = peakUsage;
        }

        public MemoryUsage getCollectionUsage() {
            return collectionUsage;
        }

        public void setCollectionUsage(MemoryUsage collectionUsage) {
            this.collectionUsage = collectionUsage;
        }

        public List<String> getMemoryManagerNames() {
            return memoryManagerNames;
        }

        public void setMemoryManagerNames(List<String> memoryManagerNames) {
            this.memoryManagerNames = memoryManagerNames;
        }

        public Long getUsageThreshold() {
            return usageThreshold;
        }

        public void setUsageThreshold(Long usageThreshold) {
            this.usageThreshold = usageThreshold;
        }

        public Long getUsageThresholdCount() {
            return usageThresholdCount;
        }

        public void setUsageThresholdCount(Long usageThresholdCount) {
            this.usageThresholdCount = usageThresholdCount;
        }

        public Boolean getUsageThresholdSupported() {
            return usageThresholdSupported;
        }

        public void setUsageThresholdSupported(Boolean usageThresholdSupported) {
            this.usageThresholdSupported = usageThresholdSupported;
        }

        public Boolean getUsageThresholdExceeded() {
            return usageThresholdExceeded;
        }

        public void setUsageThresholdExceeded(Boolean usageThresholdExceeded) {
            this.usageThresholdExceeded = usageThresholdExceeded;
        }

        public Long getCollectionUsageThreshold() {
            return collectionUsageThreshold;
        }

        public void setCollectionUsageThreshold(Long collectionUsageThreshold) {
            this.collectionUsageThreshold = collectionUsageThreshold;
        }

        public Long getCollectionUsageThresholdCount() {
            return collectionUsageThresholdCount;
        }

        public void setCollectionUsageThresholdCount(Long collectionUsageThresholdCount) {
            this.collectionUsageThresholdCount = collectionUsageThresholdCount;
        }

        public Boolean getCollectionUsageThresholdExceeded() {
            return collectionUsageThresholdExceeded;
        }

        public void setCollectionUsageThresholdExceeded(Boolean collectionUsageThresholdExceeded) {
            this.collectionUsageThresholdExceeded = collectionUsageThresholdExceeded;
        }

        public Boolean getCollectionUsageThresholdSupported() {
            return collectionUsageThresholdSupported;
        }

        public void setCollectionUsageThresholdSupported(Boolean collectionUsageThresholdSupported) {
            this.collectionUsageThresholdSupported = collectionUsageThresholdSupported;
        }
    }

}
