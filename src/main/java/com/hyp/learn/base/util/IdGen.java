package com.hyp.learn.base.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 我们在开发中，有时非常需要一个全局唯一的ID值，不管是业务需求，还是为了以后可能的分表需求，全局唯一值都非常有用
 * 这个全局唯一ID生成器，其实是Twitter公开的一个算法，源码是用Scala写的，被国内的开源爱好者改写成了Java版本。
 * 这个类的调用简化了一下，实际使用中还是应该根据机器节点和数据中心节点来配置相关的参数。我这里假设只有一个节点作为ID号的生成器，所以workerId和datacenterId都设为0，当前时间与计算标记时间twepoch（Thu, 04 Nov 2010 01:42:54 GMT）之间的毫秒数是一个38位长度的long值，再左移timestampLeftShift（22位），就得到一个60位长度的long数字，该数字与datacenterId << datacenterIdShift取或，datacenterId最小值为0，最大值为31，所以长度为1-5位，datacenterIdShift是17位，所以结果就是最小值为0，最大值为22位长度的long，同理，workerId << workerIdShift的最大值为17位的long。所以最终生成的会是一个60位长度的long型唯一ID
 *
 * @author hyp
 * Project name is jarvis.base
 * Include in com.hyp.learn.base.util
 * hyp create at 19-12-17
 **/
public class IdGen {
    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long twepoch = 1288834974657L;                              //  Thu, 04 Nov 2010 01:42:54 GMT
    private long workerIdBits = 5L;                                     //  节点ID长度
    private long datacenterIdBits = 5L;                                 //  数据中心ID长度
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);             //  最大支持机器节点数0~31，一共32个
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);     //  最大支持数据中心节点数0~31，一共32个
    private long sequenceBits = 12L;                                    //  序列号12位
    private long workerIdShift = sequenceBits;                          //  机器节点左移12位
    private long datacenterIdShift = sequenceBits + workerIdBits;       //  数据中心节点左移17位
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits; //  时间毫秒数左移22位
    private long sequenceMask = -1L ^ (-1L << sequenceBits);                          //  4095
    private long lastTimestamp = -1L;

    private static class IdGenHolder {
        private static final IdGen instance = new IdGen();
    }

    public static IdGen get() {
        return IdGenHolder.instance;
    }

    public IdGen() {
        this(0L, 0L);
    }

    public IdGen(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        //获取当前毫秒数
        long timestamp = timeGen();
        //如果服务器时间有问题(时钟后退) 报错。
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果上次生成时间和当前时间相同,在同一毫秒内
        if (lastTimestamp == timestamp) {
            //sequence自增，因为sequence只有12bit，所以和sequenceMask相与一下，去掉高位
            sequence = (sequence + 1) & sequenceMask;
            //判断是否溢出,也就是每毫秒内超过4095，当为4096时，与sequenceMask相与，sequence就等于0
            if (sequence == 0) {
                //自旋等待到下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从0开始累加
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        // 最后按照规则拼出ID。
        // 000000000000000000000000000000000000000000  00000            00000       000000000000
        // time                                       datacenterId   workerId    sequence
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    //接下来我再写个测试类，看下并发情况下，1秒钟可以生成多少个ID。
    @SuppressWarnings("all")
    public static void main(String[] args) {
        long avg = 0;
        for (int k = 0; k < 10; k++) {
            List<Callable<Long>> partitions = new ArrayList<Callable<Long>>();
            final IdGen idGen = IdGen.get();
            for (int i = 0; i < 1400000; i++) {
                partitions.add(new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        return idGen.nextId();
                    }
                });
            }
            ExecutorService executorPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            try {
                long s = System.currentTimeMillis();
                executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);
                long s_avg = System.currentTimeMillis() - s;
                avg += s_avg;
                System.out.println("完成时间需要: " + s_avg / 1.0e3 + "秒");
                executorPool.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("平均完成时间需要: " + avg / 10 / 1.0e3 + "秒");

    }
}