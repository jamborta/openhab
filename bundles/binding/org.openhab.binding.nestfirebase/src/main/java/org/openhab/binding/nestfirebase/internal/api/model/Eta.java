/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nestfirebase.internal.api.model;

import java.util.Date;

public class Eta {
	
	private String trip_id;
	private Date estimated_arrival_window_begin;
	private Date estimated_arrival_window_end;
	
	private Eta(){
		// Hidden constructor used for JSON conversion
	}
	
	public Eta(String tripId, Date estimatedArrivalWindowBegin, Date estimatedArrivalWindowEnd){
		this.trip_id = tripId;
		this.estimated_arrival_window_begin = estimatedArrivalWindowBegin;
		this.estimated_arrival_window_end = estimatedArrivalWindowEnd;
	}
	
	public String getTrip_id() {
		return trip_id;
	}
	
	public Date getEstimated_arrival_window_begin() {
		return estimated_arrival_window_begin;
	}
	
	public Date getEstimated_arrival_window_end() {
		return estimated_arrival_window_end;
	}
}
