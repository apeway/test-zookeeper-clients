package org.liws.zk.clients.javaclient.t03_delete;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.liws.zk.clients.ZkProps;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper API 删除节点，使用同步(sync)接口。
 */
public class Delete_API_Sync_Usage {

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
		
		ZooKeeper zk = new ZooKeeper(ZkProps.CONNECT_STR, 5000, watcher);
    	connectedSemaphore.await();

    	String path = "/zk-book";
    	zk.create( path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL );
    	zk.delete( path, -1 );
    	
    	Thread.sleep( Integer.MAX_VALUE );
    }
}