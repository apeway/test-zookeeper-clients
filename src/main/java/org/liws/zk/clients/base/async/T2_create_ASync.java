package org.liws.zk.clients.base.async;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.liws.zk.clients.ZkProps;
import org.liws.zk.clients.base.AbsWatcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.data.ACL;

/**
 * ZooKeeper API创建节点，使用异步(async)接口。
 */
public class T2_create_ASync {

	public static void main(String[] args) throws Exception {
		AbsWatcher watcher = new AbsWatcher() {
			@Override public void process(WatchedEvent event) {
				System.out.print("接收到WatchedEvent事件通知：【" + event + "】 ->> ");
				if (KeeperState.SyncConnected == event.getState()) {
					if(EventType.None == event.getType()) {
						countDown(); 
						System.out.println("这说明zookeeper会话已经异步创建成功了！");
					}
				} 
			}
		};
		ZooKeeper zookeeper = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
		watcher.await();
        
		//////////////////////////////////////////////////////////////////////////////////

		byte[] data = "".getBytes();
		ArrayList<ACL> acl = Ids.OPEN_ACL_UNSAFE;
		Object ctx = "I am context.";
		AsyncCallback.StringCallback strCallBack = new StringCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx, String name) {
				System.out.println("创建path结果: [" + rc + ", " + path + ", " + ctx + ", real path name: " + name);
			}
		};
		
		/*
		 * void create(String path, byte data[], List<ACL> acl, CreateMode createMode, 
		 * 			StringCallback cb, Object ctx)
		 * ----------------------------------------------------------------------------------------
		 * @param StringCallback cb, Object ctx : 异步相关
		 */
		zookeeper.create("/zktest-ephemeral-", data, acl, CreateMode.EPHEMERAL, strCallBack, ctx);
		zookeeper.create("/zktest-ephemeral-", data, acl, CreateMode.EPHEMERAL, strCallBack, ctx);
		zookeeper.create("/zk-test-ephemeral-seq-", data, acl, CreateMode.EPHEMERAL_SEQUENTIAL, strCallBack, ctx);

		Thread.sleep(2000);
		System.out.println("程序终止！");
	}
	
}
