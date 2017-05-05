package org.komparator.mediator.ws.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPUuidClientHandler implements SOAPHandler<SOAPMessageContext> {
    public static final String REQUEST_HEADER_UUID = "requestHeaderUuid";
    public static final String REQUEST_NS = "urn:client";

    public static final String RESPONSE_HEADER_UUID = "responseHeaderUuid";
    public static final String RESPONSE_NS = "urn:server";

    private static final String HANDLER_FLAG = "sec";

    public static final String CLASS_NAME = SOAPUuidClientHandler.class.getSimpleName();

    private List<String> uuidList = new ArrayList<String>();

    /**
     * Gets the names of the header blocks that can be processed by this Handler
     * instance. If null, processes all.
     */
    @Override
    public Set getHeaders() {
	return null;
    }

    /**
     * The handleMessage method is invoked for normal processing of inbound and
     * outbound messages.
     */
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
		Name uuidName = soapEnvelope.createName(REQUEST_HEADER_UUID, HANDLER_FLAG, REQUEST_NS);
		SOAPHeaderElement sHeaderElement = soapHeader.addHeaderElement(uuidName);
		String uuid = UUID.randomUUID().toString();
		sHeaderElement.addTextNode(uuid);
	    } catch (SOAPException e) {
		System.out.printf("Failed to add SOAP header because of %s%n", e);
	    }

	} else {
	    // inbound message
	    try {
		SOAPEnvelope soapEnvelope = smc.getMessage().getSOAPPart().getEnvelope();
		SOAPHeader soapHeader = soapEnvelope.getHeader();
		if (soapHeader == null) {
		    System.err.println("MESSAGE HAS NO HEADER");
		    return false;
		}
		Name uuidName = soapEnvelope.createName(RESPONSE_HEADER_UUID, HANDLER_FLAG, RESPONSE_NS);
		Iterator elementIterator = soapHeader.getChildElements(uuidName);
		if (!elementIterator.hasNext()) {
		    System.err.println("MESSAGE HAS NO UUID");
		    return false;
		}
		SOAPElement sHeaderElement = (SOAPElement) elementIterator.next();
		String headerValue = sHeaderElement.getValue();
		if (uuidList.contains(headerValue)) {
		    System.err.println("UUID ALREADY CAME");
		    return false;
		}
		uuidList.add(headerValue);

	    } catch (SOAPException e) {
		System.out.printf("Failed to get SOAP header because of %s%n", e);
	    }

	}

	return true;
    }

    /** The handleFault method is invoked for fault message processing. */
    @Override
    public boolean handleFault(SOAPMessageContext smc) {
	return true;
    }

    /**
     * Called at the conclusion of a message exchange pattern just prior to the
     * JAX-WS runtime dispatching a message, fault or exception.
     */
    @Override
    public void close(MessageContext messageContext) {

    }

}