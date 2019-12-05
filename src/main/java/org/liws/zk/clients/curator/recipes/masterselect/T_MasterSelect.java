package org.liws.zk.clients.curator.recipes.masterselect;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;

public class T_MasterSelect {

	@SuppressWarnings("resource")
	@Test
	public void test() throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();

		String MASTER_PATH = "/curator_recipes_master_path";

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				/*
				 * LeaderSelector负责封装所有和Master选举相关的逻辑，包括所有和zookeeper服务器的交互过程。
				 * Curator会在成功竞争到Master时自动回调LeaderSelectorListener监听。
				 */
				LeaderSelector selector = new LeaderSelector(client, MASTER_PATH, new LeaderSelectorListenerAdapter() {
					@Override
					public void takeLeadership(CuratorFramework client) throws Exception {
						System.out.println(Thread.currentThread().getName()+ "->> 竞争到Master");
						System.out.println(Thread.currentThread().getName()+ "...执行master业务逻辑...");
						Thread.sleep(1000);
						System.out.println(Thread.currentThread().getName()+ "->> 释放Master权利");
					}
				});
				selector.autoRequeue();
				selector.start();
			}
		};
		
		/*
		 *  当多个线程（或应用）在同一个节点下进行master选举时，
		 *  如果一个线程已经选举出master了，其它线程会进入等待，直到当前master挂掉或退出才会开始新一轮选举。
		 */
		for (int i = 0; i < 5; i++) {
			new Thread(runnable).start();
		}
		
		Thread.sleep(Integer.MAX_VALUE);
	}
}