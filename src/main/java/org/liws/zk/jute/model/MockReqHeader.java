package org.liws.zk.jute.model;

import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * MockReqHeader表示一个简单的请求头类，通过实现Record来支持序列化和反序列化
 */
@Setter
@Getter
@EqualsAndHashCode
public class MockReqHeader implements Record {
	
	private long sessionId;
	private String type;

	public MockReqHeader() {
	}
	public MockReqHeader(long sessionId, String type) {
		this.sessionId = sessionId;
		this.type = type;
	}

	@Override
	public void serialize(OutputArchive outputArchive, String tag) throws java.io.IOException {
		outputArchive.startRecord(this, tag);
		
		outputArchive.writeLong(sessionId, "sessionId");
		outputArchive.writeString(type, "type");
		
		outputArchive.endRecord(this, tag);
	}

	@Override
	public void deserialize(InputArchive inputArchive, String tag) throws java.io.IOException {
		inputArchive.startRecord(tag);
		
		sessionId = inputArchive.readLong("sessionId");
		type = inputArchive.readString("type");
		
		inputArchive.endRecord(tag);
	}
}
