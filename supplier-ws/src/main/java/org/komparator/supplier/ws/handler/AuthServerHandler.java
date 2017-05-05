package org.komparator.supplier.ws.handler;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.Set;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CertUtil;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;

public class AuthServerHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String SUPPLIER_INDEX_PROPERTY = "supplier.index.property";
    private static final String GROUP_PASSWORD = "gm8AvvUD";
    private static final String SERVICE_ID = "t63_supplier";

    public static final String REQUEST_HEADER_AUTH = "requestHeaderSignature";
    public static final String REQUEST_NS = "urn:client";

    public static final String RESPONSE_HEADER_AUTH = "responseHeaderSignature";
    public static final String RESPONSE_NS = "urn:server";

    private static final String HANDLER_FLAG = "auth";
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

	    try {
		String propertyValue = (String) smc.get(SUPPLIER_INDEX_PROPERTY);
		String service_index = propertyValue.substring(12);
		String keystore_path = "T63_Supplier" + service_index + ".jks";
		SOAPMessage soapMessage = smc.getMessage();
		byte[] messageByteArray = SOAPMessageToByteArray(soapMessage);
		PrivateKey key = CertUtil.getPrivateKeyFromKeyStoreResource(keystore_path, GROUP_PASSWORD.toCharArray(),
			SERVICE_ID + service_index, GROUP_PASSWORD.toCharArray());

		byte[] soapMessageSigned = CertUtil.makeDigitalSignature(CertUtil.SIGNATURE_ALGO, key,
			messageByteArray);
		SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
		SOAPHeader soapHeader = soapEnvelope.getHeader();
		if (soapHeader == null)
		    soapHeader = soapEnvelope.addHeader();
		Name sigHeaderName = soapEnvelope.createName(RESPONSE_HEADER_AUTH, HANDLER_FLAG, RESPONSE_NS);
		SOAPHeaderElement sHeaderElement = soapHeader.addHeaderElement(sigHeaderName);
		sHeaderElement.addTextNode(printHexBinary(soapMessageSigned));

	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    return true;
	} else {
	    // inbound message
	    try {
		SOAPMessage soapMessage = smc.getMessage();
		SOAPEnvelope soapEnvelope = smc.getMessage().getSOAPPart().getEnvelope();
		SOAPHeader soapHeader = soapEnvelope.getHeader();
		if (soapHeader == null) {
		    System.err.println("MESSAGE HAS NO HEADER");
		    return true;
		}

		Name sigHeaderName = soapEnvelope.createName(REQUEST_HEADER_AUTH, HANDLER_FLAG, REQUEST_NS);
		Iterator elementIterator = soapHeader.getChildElements(sigHeaderName);
		if (!elementIterator.hasNext()) {
		    System.err.println("MESSAGE HAS NO SIGNATURE");
		    return true;
		}
		SOAPElement sHeaderElement = (SOAPElement) elementIterator.next();
		String headerValue = sHeaderElement.getValue();

		CAClient caClient = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca?WSDL");

		String certificateString = caClient.getCertificate(MEDIATOR_ID);
		Certificate certificate = CertUtil.certificateStringToObject(certificateString);

		Certificate trustedCACertificate = CertUtil.getX509CertificateFromResource(CA_CERTIFICATE);
		if (!CertUtil.verifySignedCertificate(certificate,
			CertUtil.getPublicKeyFromCertificate(trustedCACertificate))) {
		    return true;
		}

		byte[] messageByteArray = SOAPMessageToByteArray(soapMessage);
		if (!CertUtil.verifyDigitalSignature(CertUtil.SIGNATURE_ALGO, certificate, messageByteArray,
			parseHexBinary(headerValue))) {
		    return true;
		}

	    } catch (Exception e) {
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

    private static byte[] SOAPMessageToByteArray(SOAPMessage msg) throws Exception {
	ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
	byte[] msgByteArray = null;

	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();

	Source source = msg.getSOAPPart().getContent();
	Result result = new StreamResult(byteOutStream);
	transformer.transform(source, result);

	msgByteArray = byteOutStream.toByteArray();
	return msgByteArray;
    }

}