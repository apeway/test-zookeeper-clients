package org.liws.zk.clients.javaclient.t02_create;

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
 * ZooKeeper API创建节点，使用异步(async)接口。
 */
public class ZooKeeper_Create_API_ASync_Usage {

	public static void main(String[] args) throws Exception {
		CountDownLatch connectedSemaphore = new CountDownLatch(1);
		
		Watcher watcher = new Watcher() {
			@Override public void process(WatchedEvent event) {
				System.out.println("接收到 watched event：" + event);
				if (KeeperState.SyncConnected == event.getState()) {
					connectedSemaphore.countDown();
					System.out.println("->> zookeeper会话创建成功！");
				} else {
					System.out.println("->> zookeeper会话创建失败！");
				}
			}
		};
		
		ZooKeeper zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
		connectedSemaphore.await();

		AsyncCallback.StringCallback strCallBack = new AsyncCallback.StringCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx, String name) {
				System.out.println("创建path结果: [" + rc + ", " + path + ", " 
					+ ctx + ", real path name: " + name);
			}
		};
		
		zookeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
				strCallBack, "I am context.");

		zookeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
				strCallBack, "I am context.");

		zookeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
				strCallBack, "I am context.");
		
		Thread.sleep(2000);
		System.out.println("程序终止！");
	}
	
}
