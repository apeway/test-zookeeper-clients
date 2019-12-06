package org.liws.zk.clients.base.sync;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.liws.zk.clients.ZkProps;
import org.liws.zk.clients.base.AbsWatcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper API 获取子节点列表，使用同步(sync)接口。
 */
public class T4_getChildren_Sync {

	private static ZooKeeper zk = null;
	
	public static void main(String[] args) throws Exception {
    	AbsWatcher watcher = new AbsWatcher() {
			@Override public void process(WatchedEvent event) {
				System.out.print("接收到WatchedEvent事件通知：【" + event + "】 ->> ");
				if (EventType.None == event.getType() && null == event.getPath()) {
					countDown();
					System.out.println("这说明zookeeper会话已经异步创建成功了！");
				} 
				// XXX 
				else if (event.getType() == EventType.NodeChildrenChanged) {
					try {
						System.out.println("处理NodeChildrenChanged事件，重新getChildren:" 
								+ zk.getChildren(event.getPath(), true));
					} catch (Exception e) {
					}
				}
			}
		};

		ZooKeeper zk = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
		watcher.await();
		
		final String PATH = "/zk-book";
		zk.create(PATH, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zk.create(PATH + "/c1", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		//////////////////////////////////////////////////////////////////////////
		
		/*
		 * List<String> getChildren(String path, boolean watch)
		 * 	 List<String> getChildren(String path, Watcher watcher)
		 * List<String> getChildren(String path, boolean watch, Stat stat)
		 *   List<String> getChildren(final String path, Watcher watcher, Stat stat)
		 * -------------------------------------------------------------------------------
		 * @param Watcher watcher | boolean watch :  
		 * 
		 * @param Stat stat : 
		 * 
		 */
		System.out.println("getChildren：" + zk.getChildren(PATH, true));

		// 新增子节点，触发NodeChildrenChanged事件
		zk.create(PATH + "/c2", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		Thread.sleep(2000);
	}
}
