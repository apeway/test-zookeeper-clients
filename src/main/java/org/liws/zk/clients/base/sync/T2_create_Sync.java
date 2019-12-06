package org.liws.zk.clients.base.sync;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.liws.zk.clients.ZkProps;
import org.liws.zk.clients.base.AbsWatcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;

/**
 * ZooKeeper API创建节点，使用同步(sync)接口。
 */
public class T2_create_Sync {

    public static void main(String[] args) throws Exception{
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

		/*
		 * String create(final String path, byte data[], List<ACL> acl, CreateMode createMode)
		 * ------------------------------------------------------------------------------------
		 * 
		 */
		// 创建临时节点EPHEMERAL
		String realPath = zookeeper.create("/zktest-ephemeral-", data, acl, CreateMode.EPHEMERAL);
		System.out.println("成功创建节点: " + realPath); // /zktest-ephemeral-

		// 创建临时顺序节点EPHEMERAL_SEQUENTIAL
		realPath = zookeeper.create("/zktest-ephemeral-seq-", data, acl, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println("成功创建节点: " + realPath); // /zktest-ephemeral-seq-0000000039
    }
    
}
