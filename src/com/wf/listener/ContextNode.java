package com.wf.listener;

public class ContextNode {
	private String key ;
	
	private Object value ;
	
	public ContextNode(String key, Object value) {
		this.key = key;
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	public Object getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "[ "+key+" => "+value+" ]" ;
	}
}
