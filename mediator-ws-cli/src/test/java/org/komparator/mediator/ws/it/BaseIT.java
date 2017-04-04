package org.komparator.mediator.ws.it;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komparator.mediator.ws.cli.MediatorClient;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

public class BaseIT {

    private static final String TEST_PROP_FILE = "/test.properties";
    protected static Properties testProps;

    protected static MediatorClient mediatorClient;

    protected static List<String> suplierList;

    @BeforeClass
    public static void oneTimeSetup() throws Exception {
	testProps = new Properties();
	try {
	    testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
	    System.out.println("Loaded test properties:");
	    System.out.println(testProps);
	} catch (IOException e) {
	    final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
	    System.out.println(msg);
	    throw e;
	}

	String uddiEnabled = testProps.getProperty("uddi.enabled");
	String uddiURL = testProps.getProperty("uddi.url");
	String wsName = testProps.getProperty("ws.name");
	String wsURL = testProps.getProperty("ws.url");

	if ("true".equalsIgnoreCase(uddiEnabled)) {
	    mediatorClient = new MediatorClient(uddiURL, wsName);
	    UDDINaming uddiNaming = new UDDINaming(uddiURL);
	    Collection<UDDIRecord> records = uddiNaming.listRecords("T63_Supplier%");

	    for (UDDIRecord record : records) {
		suplierList.add(record.getOrgName());
	    }
	} else {
	    mediatorClient = new MediatorClient(wsURL);
	}
    }

    @AfterClass
    public static void cleanup() {
    }
}
