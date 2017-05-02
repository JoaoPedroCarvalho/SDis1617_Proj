package org.komparator.supplier.ws.handler;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CryptoUtil;

public class CipherClientHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String SYM_ALGORITHM = CryptoUtil.SYM_ALGORITHM;
    private static final String SYM_KEY = CryptoUtil.SYM_KEY;

    public static final String REQUEST_NS = "urn:client";

    public static final String RESPONSE_NS = "urn:server";

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
		    // for each body
		    Key key = CryptoUtil.generateSymKey();
		    Iterator itBodies = soapBody.getChildElements();
		    while (itBodies.hasNext()) {
			SOAPElement sBElement = (SOAPElement) itBodies.next();
			String sBElementValue = sBElement.getValue();
			if (sBElementValue == null) {
			    Iterator itBodyChildren = sBElement.getChildElements();
			    while (itBodyChildren.hasNext()) {
				SOAPElement sBElementChild = (SOAPElement) itBodyChildren.next();
				String sBElementValueChild = sBElementChild.getValue();
				if (sBElementValueChild != null) {
				    byte[] sBEVCrypted = CryptoUtil.cipher(Cipher.ENCRYPT_MODE, SYM_ALGORITHM, key,
					    sBElementValueChild.getBytes());
				    sBElementChild.removeContents();
				    sBElementChild.addTextNode(printHexBinary(sBEVCrypted));
				}
			    }
			} else {
			    byte[] sBEVCrypted = CryptoUtil.cipher(Cipher.ENCRYPT_MODE, SYM_ALGORITHM, key,
				    sBElementValue.getBytes());
			    sBElement.removeContents();
			    sBElement.addTextNode(printHexBinary(sBEVCrypted));
			}
		    }
		    Name keyName = soapEnvelope.createName("bodyKey", "crp", REQUEST_NS);
		    SOAPElement keyElement = soapEnvelope.getHeader().addHeaderElement(keyName);
		    keyElement.addTextNode(printHexBinary(key.getEncoded()));
		}

		SOAPHeader soapHeader = soapEnvelope.getHeader();
		// check header
		if (soapHeader != null) {
		    // for each body
		    Iterator itHeaders = soapHeader.getChildElements();
		    while (itHeaders.hasNext()) {
			SOAPElement sHElement = (SOAPElement) itHeaders.next();
			String sHElementValue = sHElement.getValue();
			if (sHElementValue != null) {
			    Key key = CryptoUtil.generateSymKey();
			    byte[] sHEVCrypted;
			    if (sHElement.getElementName()
				    .equals(soapEnvelope.createName("bodyKey", "crp", REQUEST_NS))) {
				sHEVCrypted = CryptoUtil.cipher(Cipher.ENCRYPT_MODE, SYM_ALGORITHM, key,
					parseHexBinary(sHElementValue));
			    } else {
				sHEVCrypted = CryptoUtil.cipher(Cipher.ENCRYPT_MODE, SYM_ALGORITHM, key,
					sHElementValue.getBytes());
			    }
			    sHElement.removeContents();
			    sHElement.setAttribute("key", printHexBinary(key.getEncoded()));
			    sHElement.addTextNode(printHexBinary(sHEVCrypted));
			} else {
			    System.err.println("HEADER HAS CHILD!!!!");
			}
		    }
		}
	    } catch (SOAPException e) {
		System.err.printf("Failed to add SOAP header because of %s%n", e);
	    } catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (InvalidKeyException e) {
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
	    } catch (InvalidAlgorithmParameterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	} else

	{
	    // inbound message
	    try {
		SOAPEnvelope soapEnvelope = smc.getMessage().getSOAPPart().getEnvelope();

		SOAPHeader soapHeader = soapEnvelope.getHeader();
		// check header
		if (soapHeader != null) {
		    // for each body
		    Iterator itHeaders = soapHeader.getChildElements();
		    while (itHeaders.hasNext()) {
			SOAPElement sHElement = (SOAPElement) itHeaders.next();
			String sHEVCrypted = sHElement.getValue();

			if (sHEVCrypted != null) {
			    byte[] keyString = parseHexBinary(sHElement.getAttribute("key"));
			    Key key = new SecretKeySpec(keyString, 0, keyString.length, SYM_KEY);
			    byte[] sHElementValue = CryptoUtil.cipher(Cipher.DECRYPT_MODE, SYM_ALGORITHM, key,
				    parseHexBinary(sHEVCrypted));
			    sHElement.removeAttribute("key");
			    sHElement.removeContents();
			    if (sHElement.getElementName()
				    .equals(soapEnvelope.createName("bodyKey", "crp", RESPONSE_NS))) {
				sHElement.addTextNode(printHexBinary(sHElementValue));
			    } else {
				sHElement.addTextNode(new String(sHElementValue));
			    }
			} else {
			    System.err.println("HEADER HAS CHILD!!!!");
			}
		    }
		}
		SOAPBody soapBody = soapEnvelope.getBody();
		// check Body
		if (soapBody != null) {
		    // for each body
		    Name keyName = soapEnvelope.createName("bodyKey", "crp", RESPONSE_NS);
		    Iterator it = soapHeader.getChildElements(keyName);
		    if (!it.hasNext()) {
			return true;
		    }
		    SOAPElement elementKey = (SOAPElement) it.next();
		    byte[] keyString = parseHexBinary(elementKey.getValue());
		    Key key = new SecretKeySpec(keyString, 0, keyString.length, SYM_KEY);

		    Iterator itBodies = soapBody.getChildElements();
		    while (itBodies.hasNext()) {
			SOAPElement sBElement = (SOAPElement) itBodies.next();
			String sBEVCrypted = sBElement.getValue();

			if (sBEVCrypted == null) {
			    Iterator itBodyChildren = sBElement.getChildElements();
			    while (itBodyChildren.hasNext()) {
				SOAPElement sBElementChild = (SOAPElement) itBodyChildren.next();
				String sBEVCryptedChild = sBElementChild.getValue();

				if (sBEVCryptedChild != null) {
				    byte[] sBElementValueChild = CryptoUtil.cipher(Cipher.DECRYPT_MODE, SYM_ALGORITHM,
					    key, parseHexBinary(sBEVCryptedChild));
				    sBElementChild.removeContents();
				    sBElementChild.addTextNode(new String(sBElementValueChild));
				}
			    }
			} else {
			    byte[] sBElementValue = CryptoUtil.cipher(Cipher.DECRYPT_MODE, SYM_ALGORITHM, key,
				    parseHexBinary(sBEVCrypted));
			    sBElement.removeContents();
			    sBElement.addTextNode(new String(sBElementValue));
			}
		    }

		}
	    } catch (SOAPException e) {
		System.err.printf("Failed to get SOAP header because of %s%n", e);
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
	    } catch (InvalidAlgorithmParameterException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
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