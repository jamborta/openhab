/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lightwaverf.internal;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openhab.binding.lightwaverf.internal.command.LightwaveRFCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfAllOffCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfCommandOk;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfDeviceRegistrationCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfDimCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfEnergyMonitorMessage;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfHeatInfoRequest;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfHeatingInfoResponse;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfMoodCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfOnOffCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfRelayCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfSetHeatingTemperatureCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfVersionMessage;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfWifiLinkStatusMessage;
import org.openhab.binding.lightwaverf.internal.exception.LightwaveRfMessageException;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts the openhab Type into a LightwaveRfCommand that can be sent to the
 * LightwaveRF Wifi link.
 * 
 * @author Neil Renaud
 * @since 1.7.0
 */
public class LightwaverfConvertor {

	private static final Logger logger = LoggerFactory.getLogger(LightwaverfConvertor.class);


	// LightwaveRF messageId
	private int nextMessageId = 200;
	private final Lock lock = new ReentrantLock();

	public List<LightwaveRFCommand> convertToLightwaveRfMessage(String roomId,
			String deviceId, LightwaveRfType deviceType, Type command) {
		int messageId = getAndIncrementMessageId();

		LinkedList<LightwaveRFCommand> commandList = new LinkedList<LightwaveRFCommand>();
		
		switch (deviceType) {
		case HEATING_BATTERY:
		case HEATING_CURRENT_TEMP:
		case HEATING_MODE:
		case VERSION:
			throw new IllegalArgumentException(deviceType
					+ " : is read only it can't be set");
		case HEATING_SET_TEMP:
			commandList.add(new LightwaveRfSetHeatingTemperatureCommand(messageId,
					roomId, ((DecimalType) command).doubleValue()));
			commandList.add(new LightwaveRfHeatInfoRequest(getAndIncrementMessageId(), roomId));
			break;
		case DIMMER:
		case SWITCH:
			if (command instanceof OnOffType) {
				boolean on = (command == OnOffType.ON);
				commandList.add(new LightwaveRfOnOffCommand(messageId, roomId, deviceId, on));
			} else if (command instanceof PercentType) {
				int dimmingLevel = ((PercentType) command).intValue();
				commandList.add(new LightwaveRfDimCommand(messageId, roomId, deviceId,
						dimmingLevel));
			} else {
				throw new RuntimeException("Unsupported Command: " + command);
			}
			break;
		case RELAY:
			if(command instanceof DecimalType){
				int state = ((DecimalType) command).intValue();
				commandList.add(new LightwaveRfRelayCommand(messageId, roomId, deviceId, state));
			} else {
				throw new RuntimeException("Unsupported Command: " + command);
			}
			break;
		case MOOD:
			if(command instanceof DecimalType){
				int state = ((DecimalType) command).intValue();
				commandList.add(new LightwaveRfMoodCommand(messageId, roomId, state));
			} else {
				throw new RuntimeException("Unsupported Command: " + command);
			}
			break;
		case ALL_OFF:
			if(command instanceof OnOffType){
				commandList.add(new LightwaveRfAllOffCommand(messageId, roomId));
			} else {
				throw new RuntimeException("Unsupported Command: " + command);
			}
			break;
		default:
			throw new IllegalArgumentException(deviceType + " : is unexpected");
		}
		return commandList;
	}

	public LightwaveRFCommand convertToLightwaveRfMessage(String roomId,
			LightwaveRfType deviceType, Type command) {
		if (roomId == null) {
			throw new IllegalArgumentException("Item not found");
		}

		switch (deviceType) {
		case HEATING_SET_TEMP:
			return new LightwaveRfSetHeatingTemperatureCommand(
					getAndIncrementMessageId(), roomId,
					((DecimalType) command).doubleValue());
		default:
			throw new IllegalArgumentException("Not implemented yet");
		}
	}

	public LightwaveRFCommand convertFromLightwaveRfMessage(String message)
			throws LightwaveRfMessageException {
		if (LightwaveRfCommandOk.matches(message)) {
			return new LightwaveRfCommandOk(message);
		} else if (LightwaveRfVersionMessage.matches(message)) {
			return new LightwaveRfVersionMessage(message);
		} else if (LightwaveRfDeviceRegistrationCommand.matches(message)) {
			return new LightwaveRfDeviceRegistrationCommand(message);
		} else if (LightwaveRfHeatingInfoResponse.matches(message)) {
			return new LightwaveRfHeatingInfoResponse(message);
		} else if (LightwaveRfSetHeatingTemperatureCommand.matches(message)) {
			return new LightwaveRfSetHeatingTemperatureCommand(message);
		} else if (LightwaveRfHeatInfoRequest.matches(message)) {
			return new LightwaveRfHeatInfoRequest(message);
		} else if (LightwaveRfDimCommand.matches(message)) {
			return new LightwaveRfDimCommand(message);
		} else if (LightwaveRfEnergyMonitorMessage.matches(message)){
			return new LightwaveRfEnergyMonitorMessage(message);
		} else if (LightwaveRfOnOffCommand.matches(message)) {
			return new LightwaveRfOnOffCommand(message);
		}  else if (LightwaveRfWifiLinkStatusMessage.matches(message)){
			return new LightwaveRfWifiLinkStatusMessage(message);
		} else if (LightwaveRfMoodCommand.matches(message)){
			return new LightwaveRfMoodCommand(message);
		} else if (LightwaveRfAllOffCommand.matches(message)){
			return new LightwaveRfAllOffCommand(message);
		}
		throw new LightwaveRfMessageException("Message not recognised: "
				+ message);
	}

	public LightwaveRFCommand getRegistrationCommand() {
		return new LightwaveRfDeviceRegistrationCommand();
	}

	public LightwaveRFCommand getHeatRequest(String roomId) {
		int messageId = getAndIncrementMessageId();
		return new LightwaveRfHeatInfoRequest(messageId, roomId);
	}

	/**
	 * Increment message counter, so different messages have different IDs
	 * Important for getting corresponding OK acknowledgements from port 9761
	 * tagged with the same counter value
	 */
	private int getAndIncrementMessageId() {
		try {
			lock.lock();
			int myMessageId = nextMessageId;
			if (myMessageId >= 999) {
				nextMessageId = 200;
			} else {
				nextMessageId++;
			}
			return myMessageId;
		} finally {
			lock.unlock();
		}
	}
}
