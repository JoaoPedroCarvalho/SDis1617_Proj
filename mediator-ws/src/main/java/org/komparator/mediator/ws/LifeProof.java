package org.komparator.mediator.ws;

import java.time.LocalDateTime;
import java.util.TimerTask;

import org.komparator.mediator.domain.Mediator;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;

import com.sun.xml.ws.client.ClientTransportException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

public class LifeProof extends TimerTask {

    private String status;
    private String secMediatorUrl;
    private int lifeproofTimer;
    private String uddiURL;
    private String wsName;
    private String wsUrl;
    private MediatorEndpointManager endpoint;

    public LifeProof(int lifeproofTimer, String status, String secMediatorUrl) {
	this.status = status;
	this.secMediatorUrl = secMediatorUrl;
	this.lifeproofTimer = lifeproofTimer;
    }

    public LifeProof(int lifeproofTimer, String status, String uddiUrl, String wsName, String wsUrl) {
	this.status = status;
	this.lifeproofTimer = lifeproofTimer;
	this.uddiURL = uddiUrl;
	this.wsName = wsName;
	this.wsUrl = wsUrl;
    }

    @Override
    public void run() {
	if (this.status.equals("primary")) {
	    try {
		MediatorClient tempClient = new MediatorClient(secMediatorUrl);
		tempClient.imAlive();
	    } catch (ClientTransportException | MediatorClientException e) {
		System.err.println("FAILED TO CREATE MEDCLI");
	    }
	} else if (this.status.equals("secondary")) {
	    Mediator mediator = Mediator.getInstance();
	    if (mediator != null) {
		LocalDateTime lastBreath = mediator.getLastBreath();
		if (lastBreath != null) {
		    lastBreath = lastBreath.plusSeconds(lifeproofTimer);
		    if (lastBreath.isBefore(LocalDateTime.now())) {
			try {
			    System.err.println("PRIMARY DIED, ASSUMING WORK");
			    UDDINaming uddiNaming = new UDDINaming(uddiURL);
			    uddiNaming.unbind(wsName);
			    uddiNaming = new UDDINaming(uddiURL);
			    uddiNaming.rebind(wsName, wsUrl);
			    status = "primary";
			    endpoint.setStatus("primary");
			    endpoint.getTimer().cancel();
			    endpoint.setUddiNaming(uddiNaming);
			} catch (UDDINamingException e) {
			    System.err.println("ERRO UDDI");
			}
		    }
		}
	    }

	}
	return;
    }
    // Members ---------------------------------------------------------------

    public void setEndpoint(MediatorEndpointManager endpoint) {
	this.endpoint = endpoint;
    }

}
