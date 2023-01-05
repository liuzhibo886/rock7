package com.lzb.rock.base.enums;

/**
 * 消息消费类型
 * 
 * @author lzb
 * @date 2020年9月2日下午2:03:03
 */
public enum MessageEnum {

	/**
	 * 广播消费
	 */
	BROADCASTING("BROADCASTING"),
	/**
	 * 集群消费
	 */
	CLUSTERING("CLUSTERING");

    private String modeCN;

    MessageEnum(String modeCN) {
        this.modeCN = modeCN;
    }

    public String getModeCN() {
        return modeCN;
    }
}
