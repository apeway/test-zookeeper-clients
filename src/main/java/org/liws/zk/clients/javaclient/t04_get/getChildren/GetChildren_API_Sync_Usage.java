package org.liws.zk.clients.javaclient.t04_get.getChildren;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.liws.zk.clients.ZkProps;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper API 获取子节点列表，使用同步(sync)接口。
 */
public class GetChildren_API_Sync_Usage {

	private static ZooKeeper zk = null;
	
	public static void main(String[] args) throws Exception {
    	CountDownLatch connectedSemaphore = new CountDownLatch(1);
		
		Watcher watcher = new Watcher() {
			@Override public void process(WatchedEvent event) {
				System.out.println("接收到 watched event：" + event);
				if (EventType.None == event.getType() && null == event.getPath()) {
					connectedSemaphore.countDown();
					System.out.println("->> zookeeper会话创建成功！");
				} else if (event.getType() == EventType.NodeChildrenChanged) {
					try {
						System.out.println("ReGet Child:" + zk.getChildren(event.getPath(), true));
					} catch (Exception e) {
					}
				}else {
					System.out.println("->> zookeeper会话创建失败！");
				}
			}
		};

		ZooKeeper zk = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
		connectedSemaphore.await();
		
		String path = "/zk-book";
		zk.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zk.create(path + "/c1", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		List<String> childrenList = zk.getChildren(path, true);
		System.out.println(childrenList);

		zk.create(path + "/c2", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		Thread.sleep(2000);
	}
}
