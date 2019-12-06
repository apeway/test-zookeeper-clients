package org.liws.zk.clients.base.sync;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.liws.zk.clients.ZkProps;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper API创建节点，使用同步(sync)接口。
 */
public class T2_Create_Sync {

    public static void main(String[] args) throws Exception{
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
        
        String path1 = zookeeper.create("/zk-test-ephemeral-", 
        		"".getBytes(), 
        		Ids.OPEN_ACL_UNSAFE, 
        		CreateMode.EPHEMERAL); 				// 创建临时节点
        System.out.println("成功创建znode: " + path1);	// /zk-test-ephemeral-

        String path2 = zookeeper.create("/zk-test-ephemeral-", 
        		"".getBytes(), 
        		Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL); 	// 创建临时顺序节点
        System.out.println("成功创建znode: " + path2);	// /zk-test-ephemeral-0000000039
    }
    
}
