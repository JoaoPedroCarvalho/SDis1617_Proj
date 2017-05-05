package org.komparator.mediator.ws.handler;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;

import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CertUtil;
import org.komparator.security.CryptoUtil;

public class CipherServerHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String REQUEST_NS = "urn:client";
    public static final String RESPONSE_NS = "urn:server";
    private static final String GROUP_PASSWORD = "gm8AvvUD";
    private static final String SERVICE_ID = "t63_mediator";
    private static final String KEYSTORE_PATH = "T63_Mediator.jks";

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
	    return true;
	} else {
	    // inbound message
	    try {
		SOAPEnvelope soapEnvelope = smc.getMessage().getSOAPPart().getEnvelope();
		SOAPBody soapBody = soapEnvelope.getBody();
		// check Body
		if (soapBody != null) {
		    Iterator sBodyElements = soapBody.getChildElements();
		    while (sBodyElements.hasNext()) {
			SOAPElement bodyElement = (SOAPElement) sBodyElements.next();
			if (bodyElement.getElementName().getLocalName().equals("buyCart")) {
			    Iterator bodyElementChildren = bodyElement.getChildElements();
			    while (bodyElementChildren.hasNext()) {
				SOAPElement bodyElementChild = (SOAPElement) bodyElementChildren.next();
				if (bodyElementChild.getElementName().getLocalName().equals("creditCardNr")) {
				    PrivateKey key = CertUtil.getPrivateKeyFromKeyStoreResource(KEYSTORE_PATH,
					    GROUP_PASSWORD.toCharArray(), SERVICE_ID, GROUP_PASSWORD.toCharArray());
				    String ccNumberCripted = bodyElementChild.getValue();
				    System.out.println(key);
				    byte[] ccNumber = CryptoUtil.cipher(Cipher.DECRYPT_MODE, key,
					    parseHexBinary(ccNumberCripted));
				    bodyElementChild.removeContents();
				    bodyElementChild.addTextNode(new String(ccNumber));

				    return true;
				}
			    }
			}
		    }
		}
	    } catch (SOAPException | UnrecoverableKeyException | FileNotFoundException | KeyStoreException
		    | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
		    | IllegalBlockSizeException | BadPaddingException e) {
		System.err.printf("Failed to get SOAP header because of %s%n", e);

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