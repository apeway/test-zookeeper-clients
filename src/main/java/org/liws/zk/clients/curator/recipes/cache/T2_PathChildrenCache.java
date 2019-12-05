package org.liws.zk.clients.curator.recipes.cache;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.liws.zk.clients.ZkProps;

public class T2_PathChildrenCache {

    
    @SuppressWarnings({ "deprecation", "resource" })
	@Test
	public void test1() throws Exception {
    	CuratorFramework client = CuratorFrameworkFactory.builder().connectString(ZkProps.CONNECT_STR)
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		
		String PATH = "/zk-book";
		
		/*
		 * -- PathChildrenCache
		 *    用于监听指定节点的子节点变化情况。
		 * -- PathChildrenCacheListener
		 *    PathChildrenCache相应的事件处理回调接口；当发生以下几种事件时，其childEvent()就会被回调。
		 *    1、子节点新增
		 *    2、子节点数据变更
		 *    3、子节点删除
		 *    注意：对父节点本身的变更，不会触发PathChildrenCacheEvent
		 */
		PathChildrenCache cache = new PathChildrenCache(client, PATH, true);
		cache.getListenable().addListener(new PathChildrenCacheListener() {
			public void childEvent(CuratorFramework client, 
					               PathChildrenCacheEvent event) throws Exception {
				switch (event.getType()) {
					case CHILD_ADDED:
						System.out.println(
								"CHILD_ADDED, " + event.getData().getPath() + ", " + new String(event.getData().getData()));
						break;
					case CHILD_UPDATED:
						System.out.println(
								"CHILD_UPDATED, " + event.getData().getPath() + ", " + new String(event.getData().getData()));
						break;
					case CHILD_REMOVED:
						System.out.println(
								"CHILD_REMOVED, " + event.getData().getPath() + ", " + new String(event.getData().getData()));
						break;
					default:
						System.out.println("发生的其它事件：" + event.getType());
						break;
				}
			}
		});
		cache.start(StartMode.POST_INITIALIZED_EVENT);
		
		client.create().withMode(CreateMode.PERSISTENT).forPath(PATH);
		Thread.sleep(1000);
		
		/////////////////////////////////////////////
		client.create().withMode(CreateMode.PERSISTENT).forPath(PATH + "/c1", "123".getBytes());
		Thread.sleep(1000);
		
		client.setData().forPath(PATH + "/c1", "456".getBytes());

		client.delete().guaranteed().forPath(PATH + "/c1");
		Thread.sleep(1000);
		/////////////////////////////////////////////
		
		client.delete().guaranteed().forPath(PATH); // 删不了？
		Thread.sleep(2000);
	}
}