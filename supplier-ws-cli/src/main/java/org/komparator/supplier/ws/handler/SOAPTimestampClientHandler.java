package org.komparator.supplier.ws.handler;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPTimestampClientHandler implements SOAPHandler<SOAPMessageContext> {
    public static final String REQUEST_HEADER_DATETIME = "requestHeaderDateTime";
    public static final String REQUEST_NS = "urn:client";

    public static final String RESPONSE_HEADER_DATETIME = "responseHeaderDateTime";
    public static final String RESPONSE_NS = "urn:server";

    private static final String HANDLER_FLAG = "sec";

    public static final String CLASS_NAME = SOAPTimestampClientHandler.class.getSimpleName();

    private static final long MAX_ACCEPTED_TIME_IN_SECONDS = 3;

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
	Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	if (outbound) {
	    // outbound message
	    try {
		SOAPEnvelope soapEnvelope = smc.getMessage().getSOAPPart().getEnvelope();
		SOAPHeader soapHeader = soapEnvelope.getHeader();
		if (soapHeader == null)
		    soapHeader = soapEnvelope.addHeader();
		Name timestampName = soapEnvelope.createName(REQUEST_HEADER_DATETIME, HANDLER_FLAG, REQUEST_NS);
		SOAPHeaderElement sHeaderElement = soapHeader.addHeaderElement(timestampName);
		String timeString = LocalDateTime.now().toString();
		sHeaderElement.addTextNode(timeString);
	    } catch (SOAPException e) {
		System.out.printf("Failed to add SOAP header because of %s%n", e);
	    }

	} else {
	    // inbound message
	    try {
		SOAPEnvelope soapEnvelope = smc.getMessage().getSOAPPart().getEnvelope();
		SOAPHeader soapHeader = soapEnvelope.getHeader();
		if (soapHeader == null) {
		    System.err.println("Header not found.");
		    return true;
		}
		Name timestampName = soapEnvelope.createName(RESPONSE_HEADER_DATETIME, HANDLER_FLAG, RESPONSE_NS);
		Iterator elementIterator = soapHeader.getChildElements(timestampName);
		if (!elementIterator.hasNext()) {
		    System.err.println("MESSAGE HAS NO TIMESTAMP");
		    return true;
		}
		SOAPElement sHeaderElement = (SOAPElement) elementIterator.next();
		String headerValue = sHeaderElement.getValue();
		LocalDateTime timePast = LocalDateTime.parse(headerValue);
		LocalDateTime timeNow = LocalDateTime.now();
		boolean validDatetime;
		if (timeNow.minusSeconds(MAX_ACCEPTED_TIME_IN_SECONDS).isAfter(timePast)) {
		    validDatetime = false;
		} else {
		    validDatetime = true;
		}
	    } catch (SOAPException e) {
		System.out.printf("Failed to get SOAP header because of %s%n", e);
	    }

	}

	return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext smc) {
	return true;
    }

    @Override
    public Set<QName> getHeaders() {
	return null;
    }

    @Override
    public void close(MessageContext messageContext) {
    }

}
