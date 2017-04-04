package org.komparator.supplier.ws;

/** Main class that starts the Supplier Web Service. */
public class SupplierApp {

    public static void main(String[] args) throws Exception {
	// Check arguments
	if (args.length == 0 || args.length == 2) {
	    System.err.println("Argument(s) missing!");
	    System.err.println("Usage: java " + SupplierApp.class.getName() + " wsURL");
	    return;
	}
	String wsURL = null;

	// Create server implementation object
	SupplierEndpointManager endpoint = null;
	if (args.length == 1) {
	    wsURL = args[0];
	    endpoint = new SupplierEndpointManager(wsURL);
	} else if (args.length >= 3) {
	    String uddiURL = args[0];
	    String wsName = args[1];
	    wsURL = args[2];
	    endpoint = new SupplierEndpointManager(uddiURL, wsName, wsURL);
	    endpoint.setVerbose(true);

	    try {
		endpoint.start();
		endpoint.awaitConnections();
	    } finally {
		endpoint.stop();
	    }

	}

    }
}
