package com.bstek.common.bean;

public class Result {
	
	private Integer code = 20000;
	
	private String message;
	
	private Object data;
	
	public Result() {
	
	}
	
	public Result(Object data) {
		this.code = 20000;
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "{\"code\":" + code + ", \"message\":\"" + message + "\", \"data\":\"" + data + "\"}";
	}
}
