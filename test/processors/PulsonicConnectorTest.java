package processors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PulsonicConnectorTest {

	@Test
	public void testParseOpenConnection() {
		final String result = PulsonicConnector
				.parseResponse(
						"OPenConnection",
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOAP-ENV:Envelope xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header><WSID xmlns=\"urn:WebSMeteoVision\" xsi:type=\"xsd:hexBinary\" mustUnderstand=\"1\">12A3888F4312B07CC68D95194575A7E15E4BB1EE</WSID></SOAP-ENV:Header><SOAP-ENV:Body><ns1:OPenConnectionResponse xmlns:ns1=\"urn:WebSMeteoVision\"><OPenConnectionResult>1</OPenConnectionResult></ns1:OPenConnectionResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>");
		assertEquals("1", result);
	}

	@Test
	public void testParseGetLastValue() {
		final String result = PulsonicConnector
				.parseResponse(
						"GetLastValue",
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOAP-ENV:Envelope xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header><WSID xmlns=\"urn:WebSMeteoVision\" xsi:type=\"xsd:hexBinary\" mustUnderstand=\"1\">12A3888F4312B07CC68D95194575A7E15E4BB1EE</WSID></SOAP-ENV:Header><SOAP-ENV:Body><ns1:GetLastValueResponse xmlns:ns1=\"urn:WebSMeteoVision\"><GetLastValueResult>201407220800, 24.5</GetLastValueResult></ns1:GetLastValueResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>");
		assertEquals("201407220800, 24.5", result);
	}
}
