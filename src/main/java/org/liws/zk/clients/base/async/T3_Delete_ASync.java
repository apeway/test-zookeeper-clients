package org.liws.zk.clients.base.async;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.liws.zk.clients.ZkProps;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper API 删除节点，使用异步(async)接口。
 */
public class T3_Delete_ASync {

	public static void main(String[] args) throws Exception {
		CountDownLatch connectedSemaphore = new CountDownLatch(1);

		Watcher watcher = new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				System.out.println("接收到 watched event：" + event);
				if (KeeperState.SyncConnected == event.getState()) {
					connectedSemaphore.countDown();
					System.out.println("->> zookeeper会话创建成功！");
				} else {
					System.out.println("->> zookeeper会话创建失败！");
				}
			}
		};

		ZooKeeper zk = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
		connectedSemaphore.await();

		String PATH = "/zk-book";
		zk.create(PATH, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		//////////////////////////////////////////////////////////////////////////
		
		AsyncCallback.VoidCallback voidCallBack = new AsyncCallback.VoidCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx) {
				System.out.println("删除path结果: [" + rc + ", " + path + ", " + ctx);
			}
		};

		zk.delete(PATH, -1, voidCallBack, "I am context.");

		Thread.sleep(Integer.MAX_VALUE);
	}
}