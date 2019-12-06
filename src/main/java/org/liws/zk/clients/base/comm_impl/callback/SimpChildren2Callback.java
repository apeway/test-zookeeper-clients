package org.liws.zk.clients.base.comm_impl.callback;

import java.util.List;

import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.data.Stat;

public class SimpChildren2Callback implements Children2Callback {

	@Override
	public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
		System.out.println("Get Children znode result: ["
				+ "response code: " + rc 
				+ ", param path: " + path
				+ ", ctx: " + ctx 
				+ ", children list: " + children 
				+ ", stat: " + stat);
	}

}
