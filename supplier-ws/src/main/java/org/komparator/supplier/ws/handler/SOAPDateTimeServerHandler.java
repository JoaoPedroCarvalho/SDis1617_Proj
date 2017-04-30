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
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPDateTimeServerHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String DATETIME_PROPERTY = "valid.datetime.property";

    public static final String REQUEST_HEADER_DATETIME = "requestHeaderDateTime";
    public static final String REQUEST_NS = "urn:client";

    public static final String RESPONSE_HEADER_DATETIME = "responseHeaderDateTime";
    public static final String RESPONSE_NS = "urn:server";

    public static final String CLASS_NAME = SOAPDateTimeServerHandler.class.getSimpleName();
    private static final long MAX_ACCEPTED_TIME_IN_SECONDS = 3;

    private static final String HANDLER_FLAG = "sec";
    private boolean verbose = false;

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
	Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	if (outbound) {
	    // outbound message
	    try {
		SOAPEnvelope se = smc.getMessage().getSOAPPart().getEnvelope();
		SOAPHeader sh = se.getHeader();
		if (sh == null)
		    sh = se.addHeader();
		Name name = se.createName(RESPONSE_HEADER_DATETIME, HANDLER_FLAG, RESPONSE_NS);
		SOAPHeaderElement element = sh.addHeaderElement(name);
		String value = LocalDateTime.now().toString();
		element.addTextNode(value);
		if (verbose) {
		    System.out.printf("%s put '%s' on response message header%n", CLASS_NAME, value);
		}

	    } catch (SOAPException e) {
		System.out.printf("Failed to add SOAP header because of %s%n", e);
	    }

	} else {
	    // inbound message
	    try {
		SOAPEnvelope se = smc.getMessage().getSOAPPart().getEnvelope();
		SOAPHeader sh = se.getHeader();
		// check header
		if (sh == null) {
		    System.out.println("Header not found.");
		    return true;
		}
		// get first header element
		Name name = se.createName(REQUEST_HEADER_DATETIME, HANDLER_FLAG, REQUEST_NS);
		Iterator it = sh.getChildElements(name);
		// check header element
		if (!it.hasNext()) {
		    if (verbose) {
			System.out.printf("Header element %s not found.%n", REQUEST_HEADER_DATETIME);
		    }
		    return true;
		}
		SOAPElement element = (SOAPElement) it.next();
		String headerValue = element.getValue();
		if (verbose) {
		    System.out.printf("%s got '%s'%n", CLASS_NAME, headerValue);
		}
		LocalDateTime timePast = LocalDateTime.parse(headerValue);
		LocalDateTime timeNow = LocalDateTime.now();
		boolean validDatetime;
		if (timeNow.minusSeconds(MAX_ACCEPTED_TIME_IN_SECONDS).isAfter(timePast)) {
		    if (verbose) {
			System.err.println("SOAP Time message exceeded past: %s | now: %s");
		    }
		    validDatetime = false;
		} else {
		    validDatetime = true;
		}
		// put token in request context
		if (verbose) {
		    System.out.printf("%s put '%s' on request context%n", CLASS_NAME, Boolean.valueOf(validDatetime));
		}
		smc.put(DATETIME_PROPERTY, String.valueOf(validDatetime));
		smc.setScope(DATETIME_PROPERTY, Scope.APPLICATION);

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
