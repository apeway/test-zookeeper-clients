package org.liws.zk.clients.base.comm_impl.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * Watcher事件处理器：用于处理来自zk服务端的watcher事件通知
 */
public class SimpWatcher1 extends AbsWatcher {

	@Override public void process(WatchedEvent event) {
		System.out.print("接收到WatchedEvent事件通知：【" + event + "】 ->> ");
		
		// 根据不同通知状态、事件状态做不同的处理
		if (KeeperState.SyncConnected == event.getState()) {
			// 如果客户端与服务端成功建立会话，则调用countDown()
			if(EventType.None == event.getType()) {
				countDown(); 
				System.out.println("这说明zookeeper会话已经异步创建成功了！");
			}
		} 
	}

}
