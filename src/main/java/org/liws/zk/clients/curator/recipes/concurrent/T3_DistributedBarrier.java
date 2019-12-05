package org.liws.zk.clients.curator.recipes.concurrent;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;

public class T3_DistributedBarrier {

	/**
	 * 使用CyclicBarrier可解决同一个JVM中多线程同步问题
	 */
	@Test
	public void test_jdk_CyclicBarrier() throws IOException, InterruptedException {

		class Runner implements Runnable {
			private String name;
			private CyclicBarrier barrier;

			public Runner(String name, CyclicBarrier barrier) {
				this.name = name;
				this.barrier = barrier;
			}

			@Override
			public void run() {
				System.out.println(name + " 准备好了.");
				try {
					barrier.await();
				} catch (Exception e) {
				}
				System.out.println(name + " 起跑!");
			}
		}

		final int SIZE = 10;
		ExecutorService executor = Executors.newFixedThreadPool(SIZE);
		CyclicBarrier barrier = new CyclicBarrier(SIZE);
		for (int i = 0; i < SIZE; i++) {
			executor.submit(new Thread(new Runner("选手" + i, barrier)));
		}
		executor.shutdown();

		Thread.sleep(Integer.MAX_VALUE);
	}

	/**
	 * DistributedBarrier使用
	 */
	@Test
	public void test_DistributedBarrier() throws Exception {
		final String BARRIER_PATH = "/curator_recipes_barrier_path";
		final int SIZE = 10;

		for (int i = 0; i < SIZE; i++) {
			final int x = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
								.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
						client.start();

						DistributedBarrier barrier = new DistributedBarrier(client, BARRIER_PATH);
						System.out.println(x + "号barrier设置");
						barrier.setBarrier(); // 设置

						barrier.waitOnBarrier(); // 等待释放barrier
						System.err.println(x + "启动...");
					} catch (Exception e) {
					}
				}
			}).start();
		}

		Thread.sleep(2000);
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		DistributedBarrier barrier = new DistributedBarrier(client, BARRIER_PATH);
		barrier.removeBarrier(); // 主线程控制释放barrier

		Thread.sleep(Integer.MAX_VALUE);
	}

	/**
	 * DistributedDoubleBarrier的enter()、leave()，控制同时进入，同时退出。
	 */
	@Test
	public void test_DistributedBarrier2() throws Exception {
		final String BARRIER_PATH = "/curator_recipes_barrier_path";
		final int SIZE = 10;

		for (int i = 0; i < SIZE; i++) {
			final int x = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
								.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
						client.start();
						
						DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, BARRIER_PATH, SIZE);

						System.out.println(x + "号进入barrier");
						// 阻塞，直到所有SIZE个成员中最后一个成员调用了enter，一起解除阻塞
						barrier.enter(); 

						System.out.println(x + "启动...");
						// 阻塞，直到所有SIZE个成员中最后一个成员调用了leave，一起解除阻塞
						barrier.leave();

						System.out.println(x + "退出...");
					} catch (Exception e) {
					}
				}
			}).start();
		}
		
		Thread.sleep(Integer.MAX_VALUE);
	}
}
