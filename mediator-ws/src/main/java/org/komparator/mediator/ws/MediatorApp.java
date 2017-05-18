package org.komparator.mediator.ws;

import java.util.Timer;

public class MediatorApp {

    public static void main(String[] args) throws Exception {
	// Check arguments
	if (args.length == 0 || args.length == 2) {
	    System.err.println("Argument(s) missing!");
	    System.err.println("Usage: java " + MediatorApp.class.getName() + " wsURL OR uddiURL wsName wsURL");
	    return;
	}
	String uddiURL = null;
	String wsName = null;
	String wsURL = null;

	// Create server implementation object, according to options
	MediatorEndpointManager endpoint = null;
	if (args.length == 1) {
	    wsURL = args[0];
	    endpoint = new MediatorEndpointManager(wsURL);
	} else if (args.length >= 3) {
	    uddiURL = args[0];
	    wsName = args[1];
	    wsURL = args[2];
	    String wsI = args[3];
	    if (wsI.equals("1")) {
		Timer timer = new Timer(true);
		LifeProof medLifeProof = new LifeProof("primary", "http://localhost:8072/mediator-ws/endpoint");
		timer.schedule(medLifeProof, /* delay */ 0 * 1000, /* period */ 5 * 1000);

		endpoint = new MediatorEndpointManager(uddiURL, wsName, wsURL);
	    } else if (wsI.equals("2")) {
		endpoint = new MediatorEndpointManager(wsURL);
		System.out.println("STARTING SECONDARY MEDIATOR");
	    }
	    endpoint.setVerbose(true);
	}

	try {
	    endpoint.start();
	    endpoint.awaitConnections();
	} finally {
	    endpoint.stop();
	}

    }

}
