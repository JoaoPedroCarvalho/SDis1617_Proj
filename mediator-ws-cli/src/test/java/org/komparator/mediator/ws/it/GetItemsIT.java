package org.komparator.mediator.ws.it;

import org.junit.AfterClass;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.cli.SupplierClientException;

/**
 * Test suite
 */
public class GetItemsIT extends BaseIT {

    public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception, SupplierClientException {
	// clear remote service state before all tests
	mediatorClient.clear();
    }

    @AfterClass
    public static void oneTimeTearDown() {
	// clear remote service state after all tests
	mediatorClient.clear();
    }

    // Bad Input Tests

    @Test(expected = InvalidItemId_Exception.class)
    public void getProductNullTest() throws InvalidItemId_Exception {
	mediatorClient.getItems(null);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void getProductEmptyTest() throws InvalidItemId_Exception {
	mediatorClient.getItems("");
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void getProductWhitespaceTest() throws InvalidItemId_Exception {
	mediatorClient.getItems(" ");
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void getProductTabTest() throws InvalidItemId_Exception {
	mediatorClient.getItems("\t");
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void getProductNewlineTest() throws InvalidItemId_Exception {
	mediatorClient.getItems("\n");
    }

    // Main Tests

}
