package com.emirates.model;

import java.io.Serializable;

public class RequestDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -241172065442239393L;
	private String flightNo;
	private String flightDates;
	private String ShortFinals;
	public String getFlightNo() {
		return flightNo;
	}
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	public String getFlightDates() {
		return flightDates;
	}
	public void setFlightDates(String flightDates) {
		this.flightDates = flightDates;
	}
	public String getShortFinals() {
		return ShortFinals;
	}
	public void setShortFinals(String shortFinals) {
		ShortFinals = shortFinals;
	}
	
	

}
