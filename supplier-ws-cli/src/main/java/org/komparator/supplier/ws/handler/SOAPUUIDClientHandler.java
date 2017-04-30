package org.komparator.supplier.ws.handler;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

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

public class SOAPUUIDClientHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String UUID_PROPERTY = "uuid.property";

    public static final String REQUEST_HEADER_UUID = "requestHeaderUuid";
    public static final String REQUEST_NS = "urn:client";

    public static final String RESPONSE_HEADER_UUID = "responseHeaderUuid";
    public static final String RESPONSE_NS = "urn:server";

    public static final String CLASS_NAME = SOAPUUIDClientHandler.class.getSimpleName();

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
		Name name = se.createName(REQUEST_HEADER_UUID, HANDLER_FLAG, REQUEST_NS);
		SOAPHeaderElement element = sh.addHeaderElement(name);
		String value = UUID.randomUUID().toString();
		element.addTextNode(value);
		if (verbose) {
		    System.out.printf("%s put '%s' on request message header%n", CLASS_NAME, value);
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
		Name name = se.createName(RESPONSE_HEADER_UUID, HANDLER_FLAG, RESPONSE_NS);
		Iterator it = sh.getChildElements(name);
		// check header element
		if (!it.hasNext()) {
		    if (verbose) {
			System.out.printf("Header element %s not found.%n", RESPONSE_HEADER_UUID);
		    }
		    return true;
		}
		SOAPElement element = (SOAPElement) it.next();
		String headerValue = element.getValue();
		if (verbose) {
		    System.out.printf("%s got '%s'%n", CLASS_NAME, headerValue);
		}
		if (verbose) {
		    System.out.printf("%s put '%s' on response context%n", CLASS_NAME, headerValue);
		}
		smc.put(UUID_PROPERTY, headerValue);
		smc.setScope(UUID_PROPERTY, Scope.APPLICATION);

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
