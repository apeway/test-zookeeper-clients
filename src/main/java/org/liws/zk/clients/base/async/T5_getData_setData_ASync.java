package org.liws.zk.clients.base.async;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.liws.zk.clients.StatUtils;
import org.liws.zk.clients.ZkProps;
import org.liws.zk.clients.base.AbsWatcher;
import org.apache.zookeeper.ZooKeeper;

// ZooKeeper API 获取节点数据内容，使用异步(async)接口。
public class T5_getData_setData_ASync {

	private static ZooKeeper zk;

	public static void main(String[] args) throws Exception {
		AsyncCallback.DataCallback dataCallback = new AsyncCallback.DataCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
				System.out.println(rc + ", " + path + ", " + new String(data));
				StatUtils.printStat(stat);
			}
		};
		
		AsyncCallback.StatCallback statCallback = new AsyncCallback.StatCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx, Stat stat) {
				if (rc == 0) {
		            System.out.println("SUCCESS");
		        }
			}
		};
		
		//////////////////////////////////////////////////////////////////////////
		
		AbsWatcher watcher = new AbsWatcher() {
			@Override public void process(WatchedEvent event) {
				System.out.print("接收到WatchedEvent事件通知：【" + event + "】 ->> ");
				if (EventType.None == event.getType() && null == event.getPath()) {
					countDown();
					System.out.println("这说明zookeeper会话已经异步创建成功了！");
				} 
				// XXX 
				else if (event.getType() == EventType.NodeDataChanged) {
					try {
						zk.getData(event.getPath(), true, dataCallback, null);
					} catch (Exception e) {
					}
				}
			}
		};
		
		ZooKeeper zk = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
		watcher.await();
		
		final String PATH = "/zk-book";
		zk.create(PATH, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		//////////////////////////////////////////////////////////////////////////
		
		/*
		 * void getData(String path, boolean watch, DataCallback cb, Object ctx)
		 *   void getData(String path, Watcher watcher, DataCallback cb, Object ctx)
		 * -------------------------------------------------------------------------------
		 * @param Watcher watcher | boolean watch :
		 * @param DataCallback cb, Object ctx :
		 */
		zk.getData(PATH, true, dataCallback, null);

		/*
		 * void setData(String path, byte data[], int version, StatCallback cb, Object ctx)
		 * -------------------------------------------------------------------------------
		 * @param int version :
		 * @param StatCallback cb, Object ctx : 
		 */
		zk.setData(PATH, "456".getBytes(), -1, statCallback, null);

		Thread.sleep(Integer.MAX_VALUE);
	}

}


