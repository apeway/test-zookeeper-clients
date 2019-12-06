package org.liws.zk.clients.base.async;

import java.util.List;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.liws.zk.clients.ZkProps;
import org.liws.zk.clients.base.AbsWatcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper API 获取子节点列表，使用异步(ASync)接口。
 */
public class T4_getChildren_ASync {

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
		zk.create(PATH, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT); // EPHEMERAL
		zk.create(PATH + "/c1", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		//////////////////////////////////////////////////////////////////////////
		
		AsyncCallback.Children2Callback children2Callback = new AsyncCallback.Children2Callback() {
			@Override
			public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
				System.out.println("Get Children znode result: ["
						+ "response code: " + rc 
						+ ", param path: " + path
						+ ", ctx: " + ctx 
						+ ", children list: " + children 
						+ ", stat: " + stat);
			}
		};
		
		/*
		 * void getChildren(String path, boolean watch, ChildrenCallback cb, Object ctx)  		 
		 *   getChildren(final String path, Watcher watcher, ChildrenCallback cb, Object ctx)
		 * void getChildren(String path, boolean watch, Children2Callback cb, Object ctx)
		 *   getChildren(final String path, Watcher watcher, Children2Callback cb, Object ctx)
		 * --------------------------------------------------------------------------------------
		 * @param Watcher watcher | boolean watch : 
		 *     XXX 执行getChildren操作时，可顺带注册一个watcher
		 * @param ChildrenCallback cb | Children2Callback cb :
		 * 
		 * @param Object ctx : 
		 *   
		 */
		zk.getChildren(PATH, true, children2Callback, null);

		zk.create(PATH + "/c2", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		Thread.sleep(2000);
	}

}
