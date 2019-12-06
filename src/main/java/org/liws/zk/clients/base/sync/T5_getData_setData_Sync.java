package org.liws.zk.clients.base.sync;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.liws.zk.clients.StatUtils;
import org.liws.zk.clients.ZkProps;
import org.liws.zk.clients.base.AbsWatcher;

// ZooKeeper API 获取节点数据内容，使用同步(sync)接口。
public class T5_getData_setData_Sync {

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
				else if (event.getType() == EventType.NodeDataChanged) {
					try {
						Stat stat = new Stat();
						System.out.println("处理NodeDataChanged事件，重新getData:" 
								+ new String(zk.getData(event.getPath(), true, stat)));
						StatUtils.printStat(stat);
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
		 * byte[] getData(String path, boolean watch, Stat stat)
		 *   byte[] getData(String path, Watcher watcher, Stat stat)
		 * -------------------------------------------------------------------------------
		 * @param Watcher watcher | boolean watch :  
		 * 
		 * @param Stat stat : 
		 * 
		 */
		Stat stat1 = new Stat();
		System.out.println(new String(zk.getData(PATH, true, stat1)));
		StatUtils.printStat(stat1);

		/*
		 * Stat setData(String path, byte data[], int version)
		 * ------------------------------------------------------------
		 * @param int version : 
		 * 
		 */
		Stat stat2 = zk.setData(PATH, "456".getBytes(), -1); // 会触发NodeDataChanged事件
		StatUtils.printStat(stat2);

		Stat stat3 = zk.setData(PATH, "789".getBytes(), stat2.getVersion());
		StatUtils.printStat(stat3);

		try {
			zk.setData(PATH, "ABC".getBytes(), stat2.getVersion());
		} catch (KeeperException e) {
			System.out.println("Error: " + e.code() + "," + e.getMessage());
		}
        
		Thread.sleep(Integer.MAX_VALUE);
	}

}