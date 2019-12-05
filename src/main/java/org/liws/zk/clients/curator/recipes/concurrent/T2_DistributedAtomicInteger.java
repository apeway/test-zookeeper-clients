package org.liws.zk.clients.curator.recipes.concurrent;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.liws.zk.clients.ZkProps;

/**
 * 使用Curator实现分布式计数器
 */
public class T2_DistributedAtomicInteger {

	static String DISTATOMICINT_PATH = "/curator_recipes_distatomicint_path";

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();

		DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(client, DISTATOMICINT_PATH,
				new RetryNTimes(3, 1000));
		AtomicValue<Integer> rc = atomicInteger.add(8);
		System.out.println("Result: " + rc.succeeded());
	}
}