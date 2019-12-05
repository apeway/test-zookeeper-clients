package org.liws.zk.clients.curator.recipes.cache;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;

public class T1_NodeCache {

	@SuppressWarnings({ "deprecation", "resource" })
	@Test
	public void test() throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();

		String PATH = "/zk-book/nodecache";
		
		/*
		 * -- NodeCache：
		 *   用于监听指定节点本身的变化。
		 * 
		 * -- NodeCacheListener：
		 *   NodeCache相应的事件处理回调接口；当出现以下两种情况时，其nodeChanged()方法就会被回调。
		 *   1、节点被创建
		 *   2、节点数据发生变更
		 *   注意：节点删除并不会触发NodeCacheListener。
		 */
		final NodeCache cache = new NodeCache(client, PATH, false);
		cache.getListenable().addListener(new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {
				System.out.println("节点数据变更, 新数据为: " + new String(cache.getCurrentData().getData()));
			}
		});
		/* 
		 * start() : 即start(false)
		 * start(boolean buildInitial)
		 * ----------------------------
		 * @param buildInitial
		 *   如果设为true，则NodeCache会在第一次启动时就立刻从zookeeper读取相应节点的数据并保存到Cache中。
		 */
		cache.start(true);
		// System.out.println("节点当前数据为：" + new String(cache.getCurrentData().getData()));
		
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(PATH, "init".getBytes());
		Thread.sleep(1000);
		
		client.setData().forPath(PATH, "newData".getBytes());
		Thread.sleep(1000);
		
		client.delete().deletingChildrenIfNeeded().forPath(PATH);
		Thread.sleep(1000);
	}
	
}