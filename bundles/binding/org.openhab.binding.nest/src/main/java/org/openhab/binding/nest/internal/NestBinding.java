/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nest.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.nest.NestBindingProvider;
import org.openhab.binding.nest.internal.api.NestAPI;
import org.openhab.binding.nest.internal.api.listeners.AuthenticationListener;
import org.openhab.binding.nest.internal.api.listeners.CompletionListener;
import org.openhab.binding.nest.internal.api.listeners.SmokeCOAlarmListener;
import org.openhab.binding.nest.internal.api.listeners.StructureListener;
import org.openhab.binding.nest.internal.api.listeners.ThermostatListener;
import org.openhab.binding.nest.internal.api.model.AccessToken;
import org.openhab.binding.nest.internal.api.model.SmokeCOAlarm;
import org.openhab.binding.nest.internal.api.model.Structure;
import org.openhab.binding.nest.internal.api.model.Structure.AwayState;
import org.openhab.binding.nest.internal.api.model.Structure.ETA;
import org.openhab.binding.nest.internal.api.model.Thermostat;
import org.openhab.core.binding.AbstractBinding;
import org.openhab.core.binding.BindingChangeListener;
import org.openhab.core.binding.BindingProvider;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.Type;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;
	

/**
 * Implement this class if you are going create an actively polling service
 * like querying a Website/Device.
 * 
 * @author Neil Renaud
 * @since 1.7.0
 */
public class NestBinding extends AbstractBinding<NestBindingProvider> implements BindingChangeListener, AuthenticationListener, SmokeCOAlarmListener, ThermostatListener, StructureListener, AuthResultHandler {

	private static final String ACCESS_TOKEN = "accessToken";
	private static final String PIN_CODE = "pinCode";
	
	private static final Logger logger = LoggerFactory.getLogger(NestBinding.class);
	private final SimpleDateFormat NEST_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sssz");
	private String nestAuthUrl = "";
	private NestAPI nestApi;
	private String clientId;
	private String clientSecret;
	private String pinCode;

	public NestBinding() {
	}
		
	/**
	 * Called by the SCR to activate the component with its configuration read from CAS
	 * 
	 * @param bundleContext BundleContext of the Bundle that defines this component
	 * @param configuration Configuration properties for this component obtained from the ConfigAdmin service
	 */
	public void activate(final BundleContext bundleContext, final Map<String, Object> configuration) {

		String clientIdString = (String) configuration.get("clientid");
		if (StringUtils.isNotBlank(clientIdString)) {
			clientId = clientIdString;
		}
		String clientSecretString = (String) configuration.get("clientsecret");
		if (StringUtils.isNotBlank(clientSecretString)) {
			clientSecret = clientSecretString;
		}
		
		String pinCodeString = (String) configuration.get("pincode");
		if (StringUtils.isNotBlank(pinCodeString)) {
			pinCode = pinCodeString;
		}
		
		logger.info("Creating Nest API Binding for clientId[{}] clientSecret[{}] PinCode[{}]", clientId, clientSecret, pinCodeString);
		nestAuthUrl = NestAPI.getAuthUrl(clientId);
		logger.info("To get a pin code go to URL: {}", nestAuthUrl);
		this.nestApi = new NestAPI(clientId, clientSecret, pinCode);
		connectToNestApi();
	}

	
	
	private void connectToNestApi(){
		String accessToken = getAccessToken();
		if(accessToken != null){
			nestApi.addThermostatListener(this);
			nestApi.addProtectListener(this);
			nestApi.addHouseListener(this);
			nestApi.authenticate(accessToken, this);
		}
		else{
			logger.error("Error getting Access Token please check the logs");
		}
		

	}
	
	private String getAccessToken(){
		String accessToken = loadAccessToken();
		if(accessToken == null){
			AccessToken tokenObject = nestApi.getAccessToken();
			if(tokenObject != null){
				accessToken = tokenObject.getToken();
				saveAccessToken(tokenObject.getToken());
			}
		}
		return accessToken;
	}
	
	private String loadAccessToken(){
		Preferences prefs = getPrefsNode();
		String pinCode = prefs.get(PIN_CODE, null);
		if (this.pinCode.equals(pinCode)) {
			String accessToken = prefs.get(ACCESS_TOKEN, null);
			return accessToken;
		}		
		return null;
		
	}
	
	private void saveAccessToken(String accessToken){
		Preferences prefs = getPrefsNode();
		if (accessToken != null) {
			prefs.put(ACCESS_TOKEN, accessToken);
		} else {
			prefs.remove(ACCESS_TOKEN);
		}
		if (pinCode != null) {
			prefs.put(PIN_CODE, this.pinCode);
		} else {
			prefs.remove(PIN_CODE);
		}		
	}
	
	private Preferences getPrefsNode() {
		return Preferences.userRoot().node("org.openhab.nest");
	}	
	
	@Override
	public void allBindingsChanged(BindingProvider provider) {
		super.allBindingsChanged(provider);
	}
	
	@Override
	public void bindingChanged(BindingProvider provider, String itemName) {
		super.bindingChanged(provider, itemName);
	}
	
	/**
	 * Called by the SCR when the configuration of a binding has been changed through the ConfigAdmin service.
	 * @param configuration Updated configuration properties
	 */
	public void modified(final Map<String, Object> configuration) {
		// update the internal configuration accordingly
	}
	
	/**
	 * Called by the SCR to deactivate the component when either the configuration is removed or
	 * mandatory references are no longer satisfied or the component has simply been stopped.
	 * @param reason Reason code for the deactivation:<br>
	 * <ul>
	 * <li> 0 – Unspecified
     * <li> 1 – The component was disabled
     * <li> 2 – A reference became unsatisfied
     * <li> 3 – A configuration was changed
     * <li> 4 – A configuration was deleted
     * <li> 5 – The component was disposed
     * <li> 6 – The bundle was stopped
     * </ul>
	 */
	public void deactivate(final int reason) {
		nestApi.removeThermostatListener(this);
		nestApi.removeProtectListener(this);
		nestApi.removeHouseListener(this);
		nestApi.disconnect();
		nestApi = null;
		// deallocate resources here that are no longer needed and 
		// should be reset when activating this binding again
	}

	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// the code being executed when a command was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand({},{}) is called!", itemName, command);
		internalReceive(itemName, command);
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveUpdate({},{}) is called!", itemName, newState);
		internalReceive(itemName, newState);
	}
	
	private void internalReceive(String itemName, Type newType){
		for(NestBindingProvider provider : providers){
			String id = provider.getIdForItemName(itemName);
			NestType nestType = provider.getTypeForItemName(itemName);

			if(id != null && nestType != null){
				switch (nestType) {
				case HOUSE_AWAY_STATE:
					AwayState awayState = newType.equals(OnOffType.ON) ? AwayState.HOME : AwayState.AWAY;
					nestApi.setStructureAway(id, awayState, new RequestCompletionListener("Away State: " + awayState));
					break;
				case HOUSE_ETA_EARLIEST:
				case HOUSE_ETA_LATEST:
					logger.info("ETA Setting not implemented yet");
					break;
				case THERMOSTAT_TARGET_TEMP:
					long targetTemp = ((DecimalType) newType).longValue();
					nestApi.setTargetTemperatureC(id, targetTemp, new RequestCompletionListener("Setting Temp as" + targetTemp + " on " + id));
					break;
				case THERMOSTAT_TARGET_TEMP_F:
					long targetTempF = ((DecimalType) newType).longValue();
					nestApi.setTargetTemperatureF(id, targetTempF, new RequestCompletionListener("Setting Temp F as" + targetTempF + " on " + id));
					break;
				case THERMOSTAT_TARGET_HIGH_TEMP:
					long targetTempHigh = ((DecimalType) newType).longValue();
					nestApi.setTargetTemperatureHighC(id, targetTempHigh, new RequestCompletionListener("Setting High C as" + targetTempHigh + " on " + id));
					break;
				case THERMOSTAT_TARGET_HIGH_TEMP_F:
					long targetTempHighF = ((DecimalType) newType).longValue();
					nestApi.setTargetTemperatureHighF(id, targetTempHighF, new RequestCompletionListener("Setting High F as" + targetTempHighF + " on " + id));
					break;
				case THERMOSTAT_TARGET_LOW_TEMP:
					long targetTempLow = ((DecimalType) newType).longValue();
					nestApi.setTargetTemperatureLowC(id, targetTempLow, new RequestCompletionListener("Setting Low C as" + targetTempLow + " on " + id));
					break;
				case THERMOSTAT_TARGET_LOW_TEMP_F:
					long targetTempLowF = ((DecimalType) newType).longValue();
					nestApi.setTargetTemperatureLowF(id, targetTempLowF, new RequestCompletionListener("Setting Low F as" + targetTempLowF + " on " + id));
					break;
				default:
					logger.error("Attempting to set read only itemName[{}] to [{}]", itemName, newType);
					break;
				}
			}
		}
	}
	
	@Override
	public void onStructureUpdated(Structure structure) {
		logger.debug("House update received {}", structure);
		boolean published = false;
		for(NestBindingProvider provider : providers){
			List<String> itemNames = provider.getItemNameFromNestId(structure.getStructureID());
			for(String itemName : itemNames){
				NestType type = provider.getTypeForItemName(itemName);
				State state = getState(structure, type);
				published = true;
				eventPublisher.postUpdate(itemName, state);
			}
		}
		if(!published){
			logger.info("There were no items to update for Structure with ID[{}]", structure.getStructureID());
		}
	}


	@Override
	public void onThermostatUpdated(Thermostat thermostat) {
		logger.debug("Thermostat update received {}", thermostat);
		boolean published = false;
		for(NestBindingProvider provider : providers){
			List<String> itemNames = provider.getItemNameFromNestId(thermostat.getDeviceID());
			for(String itemName : itemNames){
				NestType type = provider.getTypeForItemName(itemName);
				State state = getState(thermostat, type);
				published = true;
				eventPublisher.postUpdate(itemName, state);
			}
		}
		if(!published){
			logger.info("There were no items to update for Thermostat with ID[{}]", thermostat.getDeviceID());
		}
	}
	
	@Override
	public void onSmokeCOAlarmUpdated(SmokeCOAlarm protect) {
		logger.debug("Protect update received {}", protect);
		boolean published = false;
		for(NestBindingProvider provider : providers){
			List<String> itemNames = provider.getItemNameFromNestId(protect.getDeviceID());
			for(String itemName : itemNames){
				NestType type = provider.getTypeForItemName(itemName);
				State state = getState(protect, type);
				published = true;
				eventPublisher.postUpdate(itemName, state);
			}
		}
		if(!published){
			logger.info("There were no items to update for Nest Protect with ID[{}]", protect.getDeviceID());
		}
	}

	private State getState(Thermostat thermostat, NestType type){
		switch (type) {
		case THERMOSTAT_TARGET_TEMP:
			return new DecimalType(thermostat.getTargetTemperatureC());
		case THERMOSTAT_TARGET_TEMP_F:
			return new DecimalType(thermostat.getTargetTemperatureF());
		case THERMOSTAT_TARGET_HIGH_TEMP:
			return new DecimalType(thermostat.getTargetTemperatureHighC());
		case THERMOSTAT_TARGET_HIGH_TEMP_F:
			return new DecimalType(thermostat.getTargetTemperatureHighF());
		case THERMOSTAT_TARGET_LOW_TEMP:
			return new DecimalType(thermostat.getTargetTemperatureLowC());
		case THERMOSTAT_TARGET_LOW_TEMP_F:
			return new DecimalType(thermostat.getTargetTemperatureLowF());
		case THERMOSTAT_CURRENT_HUMIDITY:
			logger.error("Nest Type Not supported yet:" + type);
			return DecimalType.ZERO;
		case THERMOSTAT_CURRENT_TEMP:
			return new DecimalType(thermostat.getAmbientTemperatureC());
		case THERMOSTAT_CURRENT_TEMP_F:
			return new DecimalType(thermostat.getAmbientTemperatureF());
		case THERMOSTAT_LAST_UPDATED:
			return new DateTimeType(thermostat.getLastConnection());
		case THERMOSTAT_CAN_COOL:
			return thermostat.canCool() ? OnOffType.ON : OnOffType.OFF;
		case THERMOSTAT_CAN_HEAT:
			return thermostat.canHeat() ? OnOffType.ON : OnOffType.OFF;
		case THERMOSTAT_HAS_FAN:
			return thermostat.hasFan() ? OnOffType.ON : OnOffType.OFF;
		case THERMOSTAT_HVAC_MODE:
			return new StringType(thermostat.getHVACmode().toString());
		case THERMOSTAT_IS_ONLINE:
			return thermostat.isOnline() ? OnOffType.ON : OnOffType.OFF;
		case THERMOSTAT_NAME:
			return new StringType(thermostat.getName());
		case THERMOSTAT_LONG_NAME:
			return new StringType(thermostat.getNameLong());
		case THERMOSTAT_VERSION:
			return new StringType(thermostat.getSoftwareVersion());
		default:
			logger.error("Type has no valid state to retrieve, type {}", type);
			return null;
		}
	}

	private State getState(SmokeCOAlarm protect, NestType type){
		switch (type) {
		case PROTECT_BATTERY_STATE:
			return new StringType(protect.getBatteryHealth());
		case PROTECT_CO_ALARM_STATE:
			return new StringType(protect.getCOAlarmState());
		case PROTECT_SMOKE_ALARM_STATE:
			return new StringType(protect.getSmokeAlarmState());
		case PROTECT_IS_ONLINE:
			return protect.isOnline() ? OnOffType.ON : OnOffType.OFF;
		case PROTECT_LAST_CONNECTED:
			return new DateTimeType(protect.getLastConnection());
		case PROTECT_LAST_MANUAL_TEST:
			return new DateTimeType(protect.getLastManualTestTime());
		case PROTECT_NAME:
			return new StringType(protect.getName());
		case PROTECT_LONG_NAME:
			return new StringType(protect.getNameLong());
		case PROTECT_UI_COLOUR:
			return new StringType(protect.getUIColorState());
		case PROTECT_VERSION:
			return new StringType(protect.getSoftwareVersion());
		default:
			logger.error("Type has no valid state to retrieve, type {}", type);
			return null;
		}
	}

	private State getState(Structure structure, NestType type) {
		switch (type) {
		case HOUSE_ETA_EARLIEST:
			ETA etaEarliest = structure.getETA();
			if(etaEarliest != null){
				logger.warn("Nest type not fully supported yet, type: {}", type);
				return new DateTimeType(etaEarliest.getEstimatedArrivalWindowBegin());
			}
			return null;
		case HOUSE_ETA_LATEST:
			ETA etaLatest = structure.getETA();
			if(etaLatest != null){
				logger.warn("Nest type not fully supported yet, type: {}", type);
				return new DateTimeType(etaLatest.getEstimatedArrivalWindowEnd());
			}
			return null;
		case HOUSE_NAME:
			return new StringType(structure.getName());
		case HOUSE_AWAY_STATE:
			switch (structure.getAwayState()) {
			case AWAY:
			case AUTO_AWAY:
				return OnOffType.OFF;
			case UNKNOWN:
			case HOME:
				return OnOffType.ON;
			default:
				return null;
			}
		case HOUSE_AWAY_STATE_STRING:
			switch (structure.getAwayState()) {
			case AUTO_AWAY:
			case AWAY:
			case HOME:
			case UNKNOWN:
				return new StringType(structure.getAwayState().getKey());
			default :
				logger.error("AwayState has no valid state to retrieve, AwayState {}", structure.getAwayState());
				return null;
			}
		default:
			logger.error("Type has no valid state to retrieve, type {}", type);
			return null;
		}
	}
	

	private DateTimeType parseDate(String dateAsString){
		try{
			if(dateAsString == null){
				return null;
			}
			Date dateAsDate = NEST_DATE_FORMATTER.parse(dateAsString);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateAsDate);
			return new DateTimeType(calendar);
		}
		catch(ParseException e){
			logger.error("Error parsing date: {} with format {}", dateAsString, NEST_DATE_FORMATTER.toPattern());
		}
		return null;
		
	}

	private class RequestCompletionListener implements CompletionListener {
		private final String message;
		public RequestCompletionListener(String message) {
			this.message = message;
		}

		@Override
		public void onComplete() {
			logger.info("Setting set on Nest Product: {}", message);
		}
	
	
		@Override
		public void onError(int errorCode) {
			logger.info("Setting not set on Nest Product: {}", message);
		}
	}
	
	
	@Override
	public void onAuthenticationSuccess() {
		logger.info("Nest authentication successful");
	}


	@Override
	public void onAuthenticationFailure(int errorCode) {
		logger.error("Nest authentication unsuccessful, errorcode[{}]", errorCode);
	}


	@Override
	public void onAuthenticated(AuthData arg0) {
		logger.info("Nest authentication successful: {}", arg0);
	}


	@Override
	public void onAuthenticationError(FirebaseError arg0) {
		logger.error("Nest authentication unsuccessful, errorcode[{}] {} {} {}", arg0.getCode(), arg0.getMessage(), arg0.getDetails(), arg0);
		
	}

}
