package org.komparator.mediator.ws;

import java.util.TimerTask;

import org.komparator.mediator.ws.cli.MediatorClient;

public class LifeProof extends TimerTask {

    private String status;
    private String secMediatorUrl;

    public LifeProof(String status, String secMediatorUrl) {
	this.status = status;
	this.secMediatorUrl = secMediatorUrl;
    }

    @Override
    public void run() {
	if (this.status.equals("primary")) {
	    try {
		MediatorClient tempClient = new MediatorClient(secMediatorUrl);

		tempClient.imAlive();
	    } catch (Exception e) {
		System.err.println("FAILED TO CREATE MEDCLI");
	    }
	} else if (this.status.equals("secondary")) {
	    return;
	}
	return;
    }
    // Members ---------------------------------------------------------------

}
