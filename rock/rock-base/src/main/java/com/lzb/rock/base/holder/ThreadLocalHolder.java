package com.lzb.rock.base.holder;

public class ThreadLocalHolder {
	public final static InheritableThreadLocal<String> requestIdLocal = new InheritableThreadLocal<String>();
	
	public final static InheritableThreadLocal<String> authorizationLocal = new InheritableThreadLocal<String>();


}
