package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

/**
 * Test suite
 */
public class GetItemsIT extends BaseIT {
    @BeforeClass
    public static void oneTimeSetUp()
	    throws BadProductId_Exception, BadProduct_Exception, UDDINamingException, SupplierClientException {
	// clear remote service state before all tests
	mediatorClient.clear();
	// Populate supplier
	populate();
    }

    @AfterClass
    public static void oneTimeTearDown() {
	// clear remote service state after all tests
	mediatorClient.clear();
    }

    // members

    // initialization and clean-up for each test
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // Tests
    @Test(expected = InvalidItemId_Exception.class)
    public void getItemsNullTest() throws InvalidItemId_Exception {
	mediatorClient.getItems(null);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void getItemsEmptyTest() throws InvalidItemId_Exception {
	mediatorClient.getItems("");
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void getItemsWhitespaceTest() throws InvalidItemId_Exception {
	mediatorClient.getItems(" ");
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void getItemsTabTest() throws InvalidItemId_Exception {
	mediatorClient.getItems("\t");
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void getItemsNewlineTest() throws InvalidItemId_Exception {
	mediatorClient.getItems("\n");
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void getItemsNonAlphaTest() throws InvalidItemId_Exception {
	mediatorClient.getItems("#");
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void getItemsMultipleSpacingTest() throws InvalidItemId_Exception {
	mediatorClient.getItems("Animal 01");
    }

    // Empty output tests

    @Test
    public void getItemsThatDoesNotExistTest() throws InvalidItemId_Exception {
	List<ItemView> output = mediatorClient.getItems("NotAnAnimal01");
	assertTrue(output.isEmpty());
    }

    @Test
    public void getItemsCaseSensitiveTest() throws InvalidItemId_Exception {
	List<ItemView> output = mediatorClient.getItems("aNIMAL01");
	assertTrue(output.isEmpty());
    }

    // Valid tests
    // very incomplete tests

    @Test
    public void getItemsSingleItemMultipleSupTest() throws InvalidItemId_Exception {
	List<ItemView> output = mediatorClient.getItems("Animal01");
	assertEquals(2, output.size());
	assertEquals("Animal01", output.get(0).getItemId().getProductId());

    }

    @Test
    public void getItemsSingleItemSingleSup1Test() throws InvalidItemId_Exception {
	List<ItemView> output = mediatorClient.getItems("Animal04");
	assertEquals(1, output.size());
    }

    @Test
    public void getItemsSingleItemSingleSup2Test() throws InvalidItemId_Exception {
	List<ItemView> output = mediatorClient.getItems("Animal07");
	assertEquals(1, output.size());
    }
}
