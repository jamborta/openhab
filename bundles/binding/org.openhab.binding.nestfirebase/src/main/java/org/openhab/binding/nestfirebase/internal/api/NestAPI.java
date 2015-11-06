package org.openhab.binding.nestfirebase.internal.api;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openhab.binding.nestfirebase.internal.NestFirebaseBinding;
import org.openhab.binding.nestfirebase.internal.api.listeners.CompletionListener;
import org.openhab.binding.nestfirebase.internal.api.listeners.SmokeCOAlarmListener;
import org.openhab.binding.nestfirebase.internal.api.listeners.StructureListener;
import org.openhab.binding.nestfirebase.internal.api.listeners.ThermostatListener;
import org.openhab.binding.nestfirebase.internal.api.model.AccessToken;
import org.openhab.binding.nestfirebase.internal.api.model.Away;
import org.openhab.binding.nestfirebase.internal.api.model.Eta;
import org.openhab.binding.nestfirebase.internal.api.model.Keys;
import org.openhab.binding.nestfirebase.internal.api.model.SmokeCOAlarm;
import org.openhab.binding.nestfirebase.internal.api.model.Structure;
import org.openhab.binding.nestfirebase.internal.api.model.Thermostat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.Config;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.Logger.Level;
import com.firebase.client.ValueEventListener;

public final class NestAPI implements ValueEventListener {
	private static final Logger logger = LoggerFactory.getLogger(NestFirebaseBinding.class);
    private static final int BUFFER_SIZE = 4096;

    private final List<SmokeCOAlarmListener> protectListeners = new CopyOnWriteArrayList<SmokeCOAlarmListener>();
    private final List<ThermostatListener> thermostatListeners = new CopyOnWriteArrayList<ThermostatListener>();
    private final List<StructureListener> houseListeners = new CopyOnWriteArrayList<StructureListener>();
    private final Firebase mFirebaseRef;

    private final ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
    private final TypeReference<Map<String,Map<String, Object>>> firebaseMapTypeRef = new TypeReference<Map<String, Map<String, Object>>>() {};
    private final TypeReference<Map<String, Structure>> structureMapTypeRef = new TypeReference<Map<String, Structure>>() {};
    private final TypeReference<Map<String, Thermostat>> thermostatMapTypeRef = new TypeReference<Map<String, Thermostat>>() {};
    private final TypeReference<Map<String, SmokeCOAlarm>> protectMapTypeRef = new TypeReference<Map<String, SmokeCOAlarm>>() {};

    private final String clientId;
    private final String clientSecret;
    private final String code;
    
    public NestAPI(String clientId, String clientSecret, String code) {
    	this.clientId = clientId;
    	this.clientSecret = clientSecret;
    	this.code = code;
        Firebase.goOffline();
        Firebase.goOnline();
        Config defaultConfig = Firebase.getDefaultConfig();
        defaultConfig.setLogLevel(Level.ERROR);
        mFirebaseRef = new Firebase(APIUrls.NEST_FIREBASE_URL);
        mFirebaseRef.addValueEventListener(this);
    }

    /**
     * Request authentication with a token.
     * @param token the token to authenticate with
     * @param listener a listener to be notified when authentication succeeds or fails
     */
    public void authenticate(String token, AuthResultHandler listener) {
    	if(token != null){
    		logger.info("authenticating with token: " + token);
    		mFirebaseRef.authWithCustomToken(token, listener);
    	}
    	else{
    		logger.warn("Warning couldn't log in with token provided[{}]", token);
    	}
    }
    
    
	public void disconnect() {
		Firebase.goOffline();
	}

    

    public AccessToken getAccessToken(){
        try {
            String formattedUrl = String.format(APIUrls.ACCESS_URL, clientId, code, clientSecret);
            System.out.println("Getting auth from: " + formattedUrl);
            URL url = new URL(formattedUrl);
            System.out.println("Created url...");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            System.out.println("Opened connection...");
            conn.setRequestMethod("POST");

            InputStream in = new BufferedInputStream(conn.getInputStream());
            String result = readStream(in);
            AccessToken token = mapper.readValue(result, AccessToken.class);
            return token;
        } 
        catch (IOException excep) {
        	System.out.println("Unable to load access token. "+ excep);
            return null;
        }
    }	
	
    private static String readStream(InputStream stream) throws IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = stream.read(buffer, 0, buffer.length)) != -1) {
            outStream.write(buffer, 0, read);
        }
        final byte[] data = outStream.toByteArray();
        return new String(data);
    }

    /**
     * Send a request to update the target temperature of the thermostat in Fahrenheit. This value
     * is not relevant if the thermostat is in "Heat and Cool" mode. Instead, see
     * {@link #setTargetTemperatureHighF(String, Long, org.openhab.binding.nestfirebase.internal.api.NestAPI.CompletionListener)}
     * and {@link #setTargetTemperatureLowF(String, Long, org.openhab.binding.nestfirebase.internal.api.NestAPI.CompletionListener)}
     * @param thermostatID the identifier for the thermostat to adjust
     * @param tempF the new temperature, in fahrenheit (in the range of 50 to 90)
     * @param listener an optional listener for success or failure
     */
    public void setTargetTemperatureF(String thermostatID, Long tempF, CompletionListener listener) {
        final String path = buildThermostatFieldPath(thermostatID, Keys.THERMOSTAT.TARGET_TEMP_F);
        sendRequest(path, tempF, listener);
    }

    /**
     * Send a request to update the target temperature of the thermostat in Celsius. This value
     * is not relevant if the thermostat is in "Heat and Cool" mode. Instead, see
     * {@link #setTargetTemperatureHighC(String, Long, org.openhab.binding.nestfirebase.internal.api.NestAPI.CompletionListener)}
     * and {@link #setTargetTemperatureLowC(String, Long, org.openhab.binding.nestfirebase.internal.api.NestAPI.CompletionListener)}
     * @param thermostatID the identifier for the thermostat to adjust
     * @param tempC the new temperature, in celsius (in the range of 9 to 32)
     * @param listener an optional listener for success or failure
     */
    public void setTargetTemperatureC(String thermostatID, Long tempC, CompletionListener listener) {
        final String path = buildThermostatFieldPath(thermostatID, Keys.THERMOSTAT.TARGET_TEMP_C);
        sendRequest(path, tempC, listener);
    }

    /**
     * Send a request to update the target cooling temperature of the thermostat in Fahrenheit.
     * This value is only relevant when in "Heat and Cool" mode. Otherwise, see
     * {@link #setTargetTemperatureF(String, Long, org.openhab.binding.nestfirebase.internal.api.NestAPI.CompletionListener)}
     * @param thermostatID the identifier for the thermostat to adjust
     * @param tempF the new temperature, in fahrenheit
     * @param listener an optional listener for success or failure
     */
    public void setTargetTemperatureHighF(String thermostatID, Long tempF, CompletionListener listener) {
        final String path = buildThermostatFieldPath(thermostatID, Keys.THERMOSTAT.TARGET_TEMP_HIGH_F);
        sendRequest(path, tempF, listener);
    }

    /**
     * Send a request to update the target cooling temperature of the thermostat in Celsius.
     * This value is only relevant when in "Heat and Cool" mode. Otherwise, see
     * {@link #setTargetTemperatureC(String, Long, org.openhab.binding.nestfirebase.internal.api.NestAPI.CompletionListener)}
     * @param thermostatID the identifier for the thermostat to adjust
     * @param tempC the new temperature, in celsius
     * @param listener an optional listener for success or failure
     */
    public void setTargetTemperatureHighC(String thermostatID, Long tempC, CompletionListener listener) {
        final String path = buildThermostatFieldPath(thermostatID, Keys.THERMOSTAT.TARGET_TEMP_HIGH_C);
        sendRequest(path, tempC, listener);
    }

    /**
     * Send a request to update the target heating temperature of the thermostat in Fahrenheit.
     * This value is only relevant when in "Heat and Cool" mode. Otherwise, see
     * {@link #setTargetTemperatureF(String, Long, org.openhab.binding.nestfirebase.internal.api.NestAPI.CompletionListener)}
     * @param thermostatID the identifier for the thermostat to adjust
     * @param tempF the new temperature, in fahrenheit
     * @param listener an optional listener for success or failure
     */
    public void setTargetTemperatureLowF(String thermostatID, Long tempF, CompletionListener listener) {
        final String path = buildThermostatFieldPath(thermostatID, Keys.THERMOSTAT.TARGET_TEMP_LOW_F);
        sendRequest(path, tempF, listener);
    }

    /**
     * Send a request to update the target heating temperature of the thermostat in Celsius.
     * This value is only relevant when in "Heat and Cool" mode. Otherwise, see
     * {@link #setTargetTemperatureC(String, Long, org.openhab.binding.nestfirebase.internal.api.NestAPI.CompletionListener)}
     * @param thermostatID the identifier for the thermostat to adjust
     * @param tempC the new temperature, in celsius
     * @param listener an optional listener for success or failure
     */
    public void setTargetTemperatureLowC(String thermostatID, Long tempC, CompletionListener listener) {
        final String path = buildThermostatFieldPath(thermostatID, Keys.THERMOSTAT.TARGET_TEMP_LOW_C);
        sendRequest(path, tempC, listener);
    }

    /**
     * Send a request to change the Away status of a structure.
     * @see org.openhab.binding.nestfirebase.internal.api.model.Structure.AwayState
     * @param structureID the identifier of the structure
     * @param awayType the new away status for the structure
     * @param listener an optional listener for success or failure
     */
    public void setStructureAway(String structureID, Away awayType, CompletionListener listener) {
        final String path = buildStructureFieldPath(structureID, Keys.STRUCTURE.AWAY);
        sendRequest(path, awayType.getKey(), listener);
    }
    
    public void setEta(String structureID, Eta eta,CompletionListener listener) {
        final String path = buildStructureFieldPath(structureID, Keys.STRUCTURE.ETA);
        final Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put(Keys.ETA.TRIP_ID, eta.getTrip_id());
        parameterMap.put(Keys.ETA.ESTIMATED_ARRIVAL_WINDOW_BEGIN, eta.getEstimated_arrival_window_begin());
        parameterMap.put(Keys.ETA.ESTIMATED_ARRIVAL_WINDOW_END, eta.getEstimated_arrival_window_end());
        sendRequest(path, parameterMap, listener);
    }

    /**
     * Add a listener to receive updates when data changes
     * @param listener the listener to receive changes
     */
    public void addThermostatListener(ThermostatListener thermostateListener){
    	thermostatListeners.add(thermostateListener);
    }

    public void addProtectListener(SmokeCOAlarmListener protectListener){
    	protectListeners.add(protectListener);
    }

    public void addHouseListener(StructureListener houseListener){
    	houseListeners.add(houseListener);
    }
    
    public void removeThermostatListener(ThermostatListener thermostateListener){
    	thermostatListeners.remove(thermostateListener);
    }

    public void removeProtectListener(SmokeCOAlarmListener protectListener){
    	protectListeners.remove(protectListener);
    }

    public void removeHouseListener(StructureListener houseListener){
    	houseListeners.remove(houseListener);
    }    

    private void sendRequest(String path, Long value, CompletionListener listener) {
        mFirebaseRef.child(path).setValue(value, new NestCompletionListener(listener));
    }

    private void sendRequest(String path, String value, CompletionListener listener) {
        mFirebaseRef.child(path).setValue(value, new NestCompletionListener(listener));
    }

    private void sendRequest(String path, Map<String, Object> map, CompletionListener listener) {
        mFirebaseRef.child(path).setValue(map, new NestCompletionListener(listener));
    }
    
    private String buildStructureFieldPath(String structureID, String fieldName) {
        return new PathBuilder()
                .append(Keys.STRUCTURES)
                .append(structureID)
                .append(fieldName).build();
    }

    private String buildThermostatFieldPath(String thermostatID, String fieldName) {
        return new PathBuilder()
                .append(Keys.DEVICES)
                .append(Keys.THERMOSTATS)
                .append(thermostatID)
                .append(fieldName).build();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
    	logger.debug("Recieved Update from Nest: {}", dataSnapshot);
    	GenericTypeIndicator<Map<String, Object>> t = new GenericTypeIndicator<Map<String, Object>>() {};
    	final Map<String, Object> values = dataSnapshot.getValue(t);
		Map<String,Map<String, Object>> mapFromFirebase = mapper.convertValue(values, firebaseMapTypeRef);
		processDataChange(mapFromFirebase);
    }
    
	private void processDataChange(Map<String,Map<String, Object>> mapToProcess){
		Map<String, Object> structuresAsObject = mapToProcess.get(Keys.STRUCTURES);
		if(structuresAsObject != null){
			Map<String, Structure> structures = mapper.convertValue(structuresAsObject, structureMapTypeRef);
			updateHouse(structures);
		}
		Map<String, Object> devices = mapToProcess.get(Keys.DEVICES);
		if(devices != null){
				Object thermostatsAsObject = devices.get(Keys.THERMOSTATS);
				Map<String, Thermostat> thermostats = mapper.convertValue(thermostatsAsObject, thermostatMapTypeRef);
				updateThermostats(thermostats);
				
				Object protectAsObject = devices.get(Keys.SMOKE_CO_ALARMS);
				Map<String, SmokeCOAlarm> smokeCoAlarms = mapper.convertValue(protectAsObject, protectMapTypeRef);
				updateSmokeCOAlarms(smokeCoAlarms);
		}
	}

    private void updateHouse(Map<String, Structure> structures) {
        for (Map.Entry<String, Structure> entry : structures.entrySet()) {
            final Structure structure = entry.getValue();
            if (structure != null) {
                for (StructureListener listener : houseListeners) {
                        listener.onStructureUpdated(structure);
                }
            }
        }
    }

    private void updateThermostats(Map<String, Thermostat> thermostatsMap) {
        for (Map.Entry<String, Thermostat> entry : thermostatsMap.entrySet()) {
            final Thermostat thermostat = entry.getValue();
            if (thermostat != null) {
                for (ThermostatListener listener : thermostatListeners) {
                    listener.onThermostatUpdated(thermostat);
                }
            }
        }
    }

    private void updateSmokeCOAlarms(Map<String, SmokeCOAlarm> smokeCOAlarms ) {
        for (Map.Entry<String, SmokeCOAlarm> entry : smokeCOAlarms.entrySet()) {
            final SmokeCOAlarm alarm = entry.getValue();

            if (alarm != null) {
                for (SmokeCOAlarmListener listener : protectListeners) {
                    listener.onSmokeCOAlarmUpdated(alarm);
                }
            }
        }
    } 

    @Override
    public void onCancelled(FirebaseError firebaseError) {
    	logger.debug("Recieved Cancel from Nest: {}", firebaseError);
    }
    
    private class NestCompletionListener implements Firebase.CompletionListener {
        private CompletionListener mCompletionListener;

        public NestCompletionListener(CompletionListener listener) {
            mCompletionListener = listener;
        }

        @Override
        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
            if (mCompletionListener == null) {
                return;
            }

            if (firebaseError == null) {
            	logger.info("Request successful");
                mCompletionListener.onComplete();
            } else {
            	logger.warn("Error: {} {}", firebaseError.getCode(), firebaseError.getMessage());
                mCompletionListener.onError(firebaseError.getCode());
            }
        }
    }
    
    private static class PathBuilder {
        private StringBuilder mBuilder;

        public PathBuilder() {
            mBuilder = new StringBuilder();
        }

        public PathBuilder append(String entry) {
            mBuilder.append("/").append(entry);
            return this;
        }

        public String build() {
            return mBuilder.toString();
        }
    }

	public static String getAuthUrl(String clientId) {
		return String.format(APIUrls.CLIENT_CODE_URL, clientId, "STATE");
	}
}
