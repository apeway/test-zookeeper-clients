package org.liws.zk.clients.javaclient.t06_exist;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.liws.zk.clients.ZkProps;
import org.liws.zk.clients.javaclient.impl.watcher.AbsWatcherWithCDL;
import org.apache.zookeeper.ZooKeeper;

/**
 * 检测节点是否存在，使用同步(sync)接口。
 */
public class Exist_API_Sync_Usage {

	private static ZooKeeper zk;

	public static void main(String[] args) throws Exception {

		AbsWatcherWithCDL watcher = new AbsWatcherWithCDL() {
			@Override
			public void process(WatchedEvent event) {
				try {
					if (KeeperState.SyncConnected == event.getState()) {
						if (EventType.None == event.getType() && null == event.getPath()) {
							System.out.println("会话创建成功！");
							countDown();
						} 
						else if (EventType.NodeCreated == event.getType()) {
							System.out.println("Node(" + event.getPath() + ")Created");
							// 再次注册watcher以监听该path
							zk.exists(event.getPath(), true);	
						} else if (EventType.NodeDeleted == event.getType()) {
							System.out.println("Node(" + event.getPath() + ")Deleted");
							// 再次注册watcher以监听该path
							zk.exists(event.getPath(), true); 	
						} else if (EventType.NodeDataChanged == event.getType()) {
							System.out.println("Node(" + event.getPath() + ")DataChanged");
							// 再次注册watcher以监听该path
							zk.exists(event.getPath(), true);	
						}
					} else {
						// TODO else
					}
				} catch (Exception e) {
				}
			}
		};
		
		zk = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
		watcher.await();

		// 最开始，并没有path对应的节点
		String path = "/zktest";
		
		/*
		 * 这里的exists调用只是为了注册一个watcher，
		 * 该watcher可实现监听path对应节点的节点创建、节点数据更新、节点删除3种事件。
		 */
		System.out.println("监听" + path);
		System.out.println(path + "对应节点stat信息：" + zk.exists(path, true));
		
		System.out.println("触发" + path + "对应节点的节点创建事件");
		Thread.sleep(500);
		zk.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		System.out.println(path + "对应节点stat信息：" + zk.exists(path, false));

		System.out.println("触发" + path + "对应节点的节点数据更新事件");
		Thread.sleep(500);
		zk.setData(path, "123".getBytes(), -1);

		System.err.println("注意：对指定节点进行监听，该节点的子节点的各种变动都不会通知客户端！");
		Thread.sleep(500);
		zk.create(path + "/c1", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		Thread.sleep(500);
		zk.delete(path + "/c1", -1);
		
		System.out.println("触发" + path + "对应节点的节点删除事件");
		Thread.sleep(500);
		zk.delete(path, -1);

		Thread.sleep(500);
		System.out.println("主程序结束！");
	}

}