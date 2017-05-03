package com.organ.common;

public enum SysInterface {
	DELBYMEMIDS("extra!delByMemberIds");

	private String name;
	
	private SysInterface(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
