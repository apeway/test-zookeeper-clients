package org.liws.zk.clients.curator.recipes.concurrent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;


public class T1_DistributedLock_InterProcessMutex {
	
	/**
	 * 流水号生成例子。 
	 * 	  一般用时间戳方式来生成流水号，但在并发量比较大的情况下，可能会出现生成了重复流水号的问题。
	 * 看T2_Lock如何解决。
	 */
	@Test
	public void test_noLock() throws Exception {
		final CountDownLatch down = new CountDownLatch(1);
		for (int i = 0; i < 30; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						down.await();
					} catch (Exception e) {
					}
					String orderNo = new SimpleDateFormat("HH:mm:ss|SSS").format(new Date());
					System.out.println("生成的流水号是 : " + orderNo);
				}
			}).start();
		}
		
		down.countDown();
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	/**
	 * 使用Curator实现分布式锁功能
	 */
	@Test
	public void test_distributedLock() throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();

		// 分布式锁
		String LOCK_PATH = "/curator_recipes_lock_path";
		final InterProcessMutex lock = new InterProcessMutex(client, LOCK_PATH);
		
		final CountDownLatch down = new CountDownLatch(1);
		for (int i = 0; i < 30; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						down.await();
						
						lock.acquire();	// 获取锁
						String orderNo = new SimpleDateFormat("HH:mm:ss|SSS").format(new Date());
						System.out.println("生成的流水号是 : " + orderNo);
						lock.release(); // 释放锁
					} catch (Exception e) {
					}
				}
			}).start();
		}
		
		down.countDown();
		Thread.sleep(Integer.MAX_VALUE);
	}
	
}