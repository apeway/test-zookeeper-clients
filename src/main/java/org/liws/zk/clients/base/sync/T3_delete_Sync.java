package org.liws.zk.clients.base.sync;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.liws.zk.clients.ZkProps;
import org.liws.zk.clients.base.AbsWatcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper API 删除节点，使用同步(sync)接口。
 */
public class T3_delete_Sync {

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
	
		//////////////////////////////////////////////////////////////////////////////////

		/*
		 * void delete(final String path, int version)
		 * -------------------------------------------------------------------------------
		 * @param int version :
		 * 
		 */
		zookeeper.delete(PATH, -1);
    	
    	Thread.sleep( Integer.MAX_VALUE );
    }
}