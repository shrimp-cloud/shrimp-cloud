package com.wkclz.common.utils;


import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;
import com.wkclz.common.entity.SystemBaseInfo;

import javax.management.MBeanServer;
import java.io.File;
import java.lang.management.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerStateUtil {

    public static SystemBaseInfo.ClassLoading getClassLoadingMXBean() {
        SystemBaseInfo.ClassLoading classLoading = new SystemBaseInfo.ClassLoading();

        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        classLoading.setLoadedClassCount(classLoadingMXBean.getLoadedClassCount());
        classLoading.setTotalLoadedClassCount(classLoadingMXBean.getTotalLoadedClassCount());
        classLoading.setUnloadedClassCount(classLoadingMXBean.getUnloadedClassCount());
        return classLoading;
    }

    public static SystemBaseInfo.Compilation getCompilationMXBean() {
        SystemBaseInfo.Compilation compilation = new SystemBaseInfo.Compilation();

        CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
        compilation.setTotalCompilationTime(compilationMXBean.getTotalCompilationTime());
        compilation.setName(compilationMXBean.getName());
        return compilation;
    }

    public static SystemBaseInfo.OperatingSystem getOperatingSystemMXBean() {
        SystemBaseInfo.OperatingSystem operatingSystem = new SystemBaseInfo.OperatingSystem();

        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        operatingSystem.setName(operatingSystemMXBean.getName());
        operatingSystem.setArch(operatingSystemMXBean.getArch());
        operatingSystem.setAvailableProcessors(operatingSystemMXBean.getAvailableProcessors());
        double systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();
        operatingSystem.setSystemLoadAverage(new BigDecimal(systemLoadAverage).setScale(2, RoundingMode.UP));
        operatingSystem.setVersion(operatingSystemMXBean.getVersion());

        operatingSystem.setCommittedVirtualMemorySize(operatingSystemMXBean.getCommittedVirtualMemorySize());
        double processCpuLoad = operatingSystemMXBean.getProcessCpuLoad();
        operatingSystem.setProcessCpuLoad(new BigDecimal(processCpuLoad).setScale(2, RoundingMode.UP));
        operatingSystem.setProcessCpuTime(operatingSystemMXBean.getProcessCpuTime());
        operatingSystem.setTotalSwapSpaceSize(operatingSystemMXBean.getTotalSwapSpaceSize());
        operatingSystem.setFreeSwapSpaceSize(operatingSystemMXBean.getFreeSwapSpaceSize());
        // 新的两个方法不能使用，待研究

        operatingSystem.setTotalMemorySize(operatingSystemMXBean.getTotalMemorySize());
        operatingSystem.setFreeMemorySize(operatingSystemMXBean.getFreeMemorySize());

        return operatingSystem;
    }

    public static SystemBaseInfo.PlatformMBeanServer getPlatformMBeanServer() {
        SystemBaseInfo.PlatformMBeanServer platformMBeanServer = new SystemBaseInfo.PlatformMBeanServer();

        MBeanServer platform = ManagementFactory.getPlatformMBeanServer();
        platformMBeanServer.setMBeanCount(platform.getMBeanCount());
        platformMBeanServer.setDefaultDomain(platform.getDefaultDomain());
        platformMBeanServer.setDomains(Arrays.asList(platform.getDefaultDomain()));

        return platformMBeanServer;
    }

    public static SystemBaseInfo.Runtime getRuntimeMXBean() {
        SystemBaseInfo.Runtime runtime = new SystemBaseInfo.Runtime();

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        runtime.setName(runtimeMXBean.getName());
        runtime.setInputArguments(runtimeMXBean.getInputArguments());
        runtime.setClassPath(runtimeMXBean.getClassPath());
        runtime.setLibraryPath(runtimeMXBean.getLibraryPath());
        runtime.setManagementSpecVersion(runtimeMXBean.getManagementSpecVersion());

        runtime.setBootClassPathSupported(runtimeMXBean.isBootClassPathSupported());
        if (runtimeMXBean.isBootClassPathSupported()) {
            runtime.setBootClassPath(runtimeMXBean.getBootClassPath());
        }

        runtime.setStartTime(runtimeMXBean.getStartTime());
        runtime.setUptime(runtimeMXBean.getUptime());

        runtime.setSpecName(runtimeMXBean.getSpecName());
        runtime.setSpecVendor(runtimeMXBean.getSpecVendor());
        runtime.setSpecVersion(runtimeMXBean.getSpecVersion());

        runtime.setVmName(runtimeMXBean.getVmName());
        runtime.setVmVendor(runtimeMXBean.getVmVendor());
        runtime.setVmVersion(runtimeMXBean.getVmVersion());

        runtime.setSystemProperties(runtimeMXBean.getSystemProperties());

        return runtime;
    }

    public static SystemBaseInfo.Thread getThreadMXBean() {
        SystemBaseInfo.Thread thread = new SystemBaseInfo.Thread();

        ThreadMXBean threadMXBean = (ThreadMXBean)ManagementFactory.getThreadMXBean();

        thread.setThreadCount(threadMXBean.getThreadCount());
        long[] allThreadIds = threadMXBean.getAllThreadIds();
        thread.setAllThreadIds(allThreadIds);
        thread.setThreadInfos(threadMXBean.getThreadInfo(allThreadIds));

        thread.setDaemonThreadCount(threadMXBean.getDaemonThreadCount());
        thread.setPeakThreadCount(threadMXBean.getPeakThreadCount());
        thread.setTotalStartedThreadCount(threadMXBean.getTotalStartedThreadCount());

        thread.setCurrentThreadCpuTime(threadMXBean.getCurrentThreadCpuTime());
        thread.setCurrentThreadUserTime(threadMXBean.getCurrentThreadUserTime());

        return thread;
    }

    public static SystemBaseInfo.Memory getMemoryMXBean() {
        SystemBaseInfo.Memory memory = new SystemBaseInfo.Memory();

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        memory.setHeapMemoryUsage(memoryMXBean.getHeapMemoryUsage());
        memory.setNonHeapMemoryUsage(memoryMXBean.getNonHeapMemoryUsage());
        memory.setObjectPendingFinalizationCount(memoryMXBean.getObjectPendingFinalizationCount());
        return memory;
    }

    public static List<SystemBaseInfo.MemoryManager> getMemoryManagerMXBeans() {
        List<SystemBaseInfo.MemoryManager> memoryManagers = new ArrayList<>();

        List<MemoryManagerMXBean> memoryManagerMXBeans = ManagementFactory.getMemoryManagerMXBeans();
        for (MemoryManagerMXBean memoryManagerMXBean : memoryManagerMXBeans) {
            SystemBaseInfo.MemoryManager memoryManager = new SystemBaseInfo.MemoryManager();
            memoryManager.setName(memoryManagerMXBean.getName());
            memoryManager.setMemoryPoolNames(Arrays.asList(memoryManagerMXBean.getMemoryPoolNames()));
            memoryManagers.add(memoryManager);
        }
        return memoryManagers;
    }

    public static List<SystemBaseInfo.GarbageCollector> getGarbageCollectorMXBeans() {
        List<SystemBaseInfo.GarbageCollector> garbageCollectors = new ArrayList<>();

        List<java.lang.management.GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (java.lang.management.GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            GarbageCollectorMXBean gbc = (GarbageCollectorMXBean)garbageCollectorMXBean;
            SystemBaseInfo.GarbageCollector garbageCollector = new SystemBaseInfo.GarbageCollector();

            garbageCollector.setCollectionCount(gbc.getCollectionCount());
            garbageCollector.setName(gbc.getName());
            garbageCollector.setMemoryPoolNames(Arrays.asList(gbc.getMemoryPoolNames()));
            garbageCollector.setCollectionTime(gbc.getCollectionTime());
            // GcInfo lastGcInfo = gbc.getLastGcInfo();
            garbageCollectors.add(garbageCollector);
        }
        return garbageCollectors;
    }

    public static List<SystemBaseInfo.MemoryPool> getMemoryPoolMXBeans() {
        List<SystemBaseInfo.MemoryPool> memoryPools = new ArrayList<>();

        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            SystemBaseInfo.MemoryPool memoryPool = new SystemBaseInfo.MemoryPool();

            memoryPool.setName(memoryPoolMXBean.getName());
            memoryPool.setValid(memoryPoolMXBean.isValid());
            memoryPool.setType(memoryPoolMXBean.getType());
            memoryPool.setUsage(memoryPoolMXBean.getUsage());
            memoryPool.setPeakUsage(memoryPoolMXBean.getPeakUsage());
            memoryPool.setCollectionUsage(memoryPoolMXBean.getCollectionUsage());
            memoryPool.setMemoryManagerNames(Arrays.asList(memoryPoolMXBean.getMemoryManagerNames()));

            memoryPool.setUsageThresholdSupported(memoryPoolMXBean.isUsageThresholdSupported());
            if (memoryPoolMXBean.isUsageThresholdSupported()) {
                memoryPool.setUsageThreshold(memoryPoolMXBean.getUsageThreshold());
                memoryPool.setUsageThresholdCount(memoryPoolMXBean.getUsageThresholdCount());
                memoryPool.setUsageThresholdExceeded(memoryPoolMXBean.isUsageThresholdExceeded());
            }

            memoryPool.setCollectionUsageThresholdSupported(memoryPoolMXBean.isCollectionUsageThresholdSupported());
            if (memoryPoolMXBean.isCollectionUsageThresholdSupported()) {
                memoryPool.setCollectionUsageThreshold(memoryPoolMXBean.getCollectionUsageThreshold());
                memoryPool.setCollectionUsageThresholdCount(memoryPoolMXBean.getCollectionUsageThresholdCount());
                memoryPool.setCollectionUsageThresholdExceeded(memoryPoolMXBean.isCollectionUsageThresholdExceeded());
            }

            memoryPools.add(memoryPool);
        }
        return memoryPools;
    }

    public static List<SystemBaseInfo.Disk> getDisk() {
        List<SystemBaseInfo.Disk> disks = new ArrayList<>();

        File[] files = File.listRoots();
        for (File file : files) {
            SystemBaseInfo.Disk disk = new SystemBaseInfo.Disk();
            disk.setPartition(file.getPath());
            disk.setTotalSpace(file.getTotalSpace());
            disk.setFreeSpace(file.getFreeSpace());
            disk.setUsedSpace(file.getTotalSpace() - file.getFreeSpace());
            disks.add(disk);
        }
        return disks;
    }

}
