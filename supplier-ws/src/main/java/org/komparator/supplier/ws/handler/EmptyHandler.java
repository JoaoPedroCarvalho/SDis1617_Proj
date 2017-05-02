package org.komparator.supplier.ws.handler;

import java.util.Set;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class EmptyHandler implements SOAPHandler<SOAPMessageContext> {

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
	// if (outbound) {
	// // outbound message
	// try {
	// } catch (SOAPException e) {
	// System.err.printf("Failed to add SOAP header because of %s%n", e);
	// }
	//
	// } else {
	// // inbound message
	// try {
	// } catch (SOAPException e) {
	// System.err.printf("Failed to get SOAP header because of %s%n", e);
	// }
	//
	// }

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