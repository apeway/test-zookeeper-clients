package org.liws.zk.clients.base.async;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.liws.zk.clients.ZkProps;
import org.liws.zk.clients.base.AbsWatcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper API 删除节点，使用异步(async)接口。
 */
public class T3_delete_ASync {

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
        
		final String PATH = "/zk-book";
		zookeeper.create(PATH, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	
		//////////////////////////////////////////////////////////////////////////
		
		AsyncCallback.VoidCallback voidCallback = new AsyncCallback.VoidCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx) {
				System.out.println("删除path结果: [" + rc + ", " + path + ", " + ctx);
			}
		};

		/*
		 * void delete(final String path, int version, VoidCallback cb, Object ctx)
		 * ------------------------------------------------------------------------
		 * @param VoidCallback cb, Object ctx : 异步相关
		 */
		zookeeper.delete(PATH, -1, voidCallback, "I am context.");

		Thread.sleep(Integer.MAX_VALUE);
	}
}