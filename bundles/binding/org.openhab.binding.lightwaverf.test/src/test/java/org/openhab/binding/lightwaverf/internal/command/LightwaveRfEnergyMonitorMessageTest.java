package org.openhab.binding.lightwaverf.internal.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openhab.binding.lightwaverf.internal.LightwaveRfType;
import org.openhab.binding.lightwaverf.internal.message.LightwaveRfJsonMessageId;
import org.openhab.core.library.types.DecimalType;

public class LightwaveRfEnergyMonitorMessageTest {

	private String messageString = "*!{\"trans\":15131,\"mac\":\"03:41:C4\"," +
			"\"time\":1452954754,\"prod\":\"pwrMtr\",\"serial\":\"9470FE\"," +
			"\"type\":\"energy\",\"cUse\":356,\"todUse\":4126}";
	
	@Test
	public void testDecodingMessage() throws Exception {
		LightwaveRfEnergyMonitorMessage message = new LightwaveRfEnergyMonitorMessage(messageString);
		assertEquals(new LightwaveRfJsonMessageId(15131), message.getMessageId());
		assertEquals("03:41:C4", message.getMac());
		assertEquals("pwrMtr", message.getProd());
		assertEquals("9470FE", message.getSerial());
		assertEquals("energy", message.getType());
		assertEquals(356, message.getcUse());
		assertEquals(4126, message.getTodUse());
	}
	
	@Test
	public void testMatches() {
		boolean matches = LightwaveRfEnergyMonitorMessage.matches(messageString);
		assertTrue(matches);
	}
	
	@Test
	public void testGetState() throws Exception {
		LightwaveRfEnergyMonitorMessage message = new LightwaveRfEnergyMonitorMessage(messageString);
		assertEquals(new DecimalType(271), message.getState(LightwaveRfType.ENERGY_CURRENT_USAGE));
		assertEquals(new DecimalType(2812), message.getState(LightwaveRfType.ENERGY_MAX_USAGE));
		assertEquals(new DecimalType(8414), message.getState(LightwaveRfType.ENERGY_TODAY_USAGE));
		assertEquals(new DecimalType(79), message.getState(LightwaveRfType.SIGNAL));
	}
}
