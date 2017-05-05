package org.komparator.mediator.ws.handler;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
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

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

public class CipherClientHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String REQUEST_NS = "urn:client";
    public static final String RESPONSE_NS = "urn:server";
    private static final String MEDIATOR_ID = "T63_Mediator";
    private static final String CA_CERTIFICATE = "ca.cer";

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
				    CAClient caClient = new CAClient(
					    "http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca?WSDL");

				    String certificateString = caClient.getCertificate(MEDIATOR_ID);
				    Certificate certificate = CertUtil.certificateStringToObject(certificateString);

				    Certificate trustedCACertificate = CertUtil
					    .getX509CertificateFromResource(CA_CERTIFICATE);
				    if (!CertUtil.verifySignedCertificate(certificate,
					    CertUtil.getPublicKeyFromCertificate(trustedCACertificate))) {
					return false;
				    }
				    PublicKey key = CertUtil.getPublicKeyFromCertificate(certificate);
				    String ccNumber = bodyElementChild.getValue();
				    byte[] sBEVCrypted = CryptoUtil.cipher(Cipher.ENCRYPT_MODE, key,
					    ccNumber.getBytes());
				    bodyElementChild.removeContents();
				    bodyElementChild.addTextNode(printHexBinary(sBEVCrypted));
				    return true;
				}
			    }
			}
		    }
		    return true;
		}
	    } catch (SOAPException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (InvalidKeyException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (NoSuchPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IllegalBlockSizeException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (BadPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (CAClientException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (CertificateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    return true;

	} else
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