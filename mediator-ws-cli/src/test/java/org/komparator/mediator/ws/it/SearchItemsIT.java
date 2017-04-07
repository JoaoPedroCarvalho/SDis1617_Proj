package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

/**
 * Test suite
 */
public class SearchItemsIT extends BaseIT {
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
    @Test(expected = InvalidText_Exception.class)
    public void searchItemsNullTest() throws InvalidText_Exception {
	mediatorClient.searchItems(null);
    }

    @Test(expected = InvalidText_Exception.class)
    public void searchItemsEmptyTest() throws InvalidText_Exception {
	mediatorClient.searchItems("");
    }

    @Test(expected = InvalidText_Exception.class)
    public void searchItemsWhitespaceTest() throws InvalidText_Exception {
	mediatorClient.searchItems(" ");
    }

    @Test(expected = InvalidText_Exception.class)
    public void searchItemsTabTest() throws InvalidText_Exception {
	mediatorClient.searchItems("\t");
    }

    @Test(expected = InvalidText_Exception.class)
    public void searchItemsNewlineTest() throws InvalidText_Exception {
	mediatorClient.searchItems("\n");
    }

    // Empty output tests

    @Test
    public void searchItemsThatDoesNotExistTest() throws InvalidText_Exception {
	List<ItemView> output = mediatorClient.searchItems("Fox");
	assertTrue(output.isEmpty());
    }

    @Test
    public void searchItemsMultipleSpacingTest() throws InvalidText_Exception {
	List<ItemView> output = mediatorClient.searchItems("Guinea  pig");
	assertTrue(output.isEmpty());
    }

    @Test
    public void searchItemsReversedTest() throws InvalidText_Exception {
	List<ItemView> output = mediatorClient.searchItems("yellow bird");
	assertTrue(output.isEmpty());
    }

    @Test
    public void searchItemsCaseSensitiveTest() throws InvalidText_Exception {
	List<ItemView> output = mediatorClient.searchItems("BiRd yElLoW");
	assertTrue(output.isEmpty());
    }

    // Valid tests

    @Test
    public void searchItemsSingleItemMultipleSupTest() throws InvalidText_Exception {
	List<ItemView> output = mediatorClient.searchItems("Bird yellow");
	assertEquals(2, output.size());
	assertEquals("Animal01", output.get(0).getItemId().getProductId());

    }

    @Test
    public void searchItemsSingleItemSingleSup1Test() throws InvalidText_Exception {
	List<ItemView> output = mediatorClient.searchItems("Guinea pig yellow");
	assertEquals(1, output.size());
    }

    @Test
    public void searchItemsSingleItemSingleSup2Test() throws InvalidText_Exception {
	List<ItemView> output = mediatorClient.searchItems("Red pig");
	assertEquals(1, output.size());
    }

    @Test
    public void searchItemsMultipleItemMultipleSupTest() throws InvalidText_Exception {
	List<ItemView> output = mediatorClient.searchItems("yellow");
	assertEquals(5, output.size());
    }

    @Test
    public void searchItemsMultipleItemSingleSup1Test() throws InvalidText_Exception {
	List<ItemView> output = mediatorClient.searchItems("Guinea pig");
	assertEquals(2, output.size());
    }

    @Test
    public void searchItemsMultipleItemSingleSup2Test() throws InvalidText_Exception {
	List<ItemView> output = mediatorClient.searchItems("mouse");
	assertEquals(2, output.size());
    }

}
