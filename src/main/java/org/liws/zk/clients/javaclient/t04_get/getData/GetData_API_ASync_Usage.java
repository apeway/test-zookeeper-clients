package org.liws.zk.clients.javaclient.t04_get.getData;

import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.liws.zk.clients.javaclient.impl.callback.SimpDataCallback;
import org.apache.zookeeper.ZooKeeper;

// ZooKeeper API 获取节点数据内容，使用异步(async)接口。
public class GetData_API_ASync_Usage implements Watcher {

	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zk;

	public static void main(String[] args) throws Exception {

		String path = "/zk-book";
		zk = new ZooKeeper("domain1.book.zookeeper:2181", 5000, //
				new GetData_API_ASync_Usage());
		connectedSemaphore.await();

		zk.create(path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		zk.getData(path, true, new SimpDataCallback(), null);

		zk.setData(path, "123".getBytes(), -1);

		Thread.sleep(Integer.MAX_VALUE);
	}

	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			if (EventType.None == event.getType() && null == event.getPath()) {
				connectedSemaphore.countDown();
			} else if (event.getType() == EventType.NodeDataChanged) {
				try {
					zk.getData(event.getPath(), true, new SimpDataCallback(), null);
				} catch (Exception e) {
				}
			}
		}
	}
}

