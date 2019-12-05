package org.liws.zk.clients.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;

public class T1_CreateSession {

	/**
	 * 使用curator来创建一个ZooKeeper客户端【使用工厂类CuratorFrameworkFactory】
	 * @throws InterruptedException 
	 */
	@Test
	public void test1() throws InterruptedException {
		/*
		 * RetryPolicy参数为重试策略，该接口中只定义了一个方法，可查看curator默认提供的5种实现。
		 *    boolean allowRetry(int retryCount, long elapsedTimeMs, RetrySleeper sleeper)
		 * --------------------------------------------------------------------------------
		 * @param retryCount 已经重试的次数
		 * @param elapsedTimeMs 从第一次重试开始，已经花费的时间
		 * @param sleeper 用于sleep指定时间【curator不建议使用Thread.sleep来进行sleep操作】
		 * --------------------------------------------------------------------------------
		 * ExponentialBackoffRetry
		 */
		CuratorFramework client = CuratorFrameworkFactory.newClient(ZkProps.CONNECT_STR, 5000, 3000, 
				new ExponentialBackoffRetry(1000, 3));
		client.start(); // start()创建会话
        Thread.sleep(Integer.MAX_VALUE);
        
	}
	
	/**
	 * 使用Fluent风格的API接口来创建一个ZooKeeper客户端
	 * @throws InterruptedException 
	 */
	@Test
	public void test2() throws InterruptedException {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
	    Thread.sleep(Integer.MAX_VALUE);
	}

	/**
	 * 创建一个"含隔离命名空间的"ZooKeeper客户端
	 * @throws InterruptedException 
	 */
	@Test
	public void test3() throws InterruptedException {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).namespace("base").build();
		client.start();
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}
