package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

/**
 * Test suite
 */
public class AddCartIT extends BaseIT {
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
    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartNullCartIdTest() throws InvalidCartId_Exception, InvalidQuantity_Exception,
	    NotEnoughItems_Exception, InvalidItemId_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart(null, itemIdView, 0);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartEmptyCartIdTest() throws InvalidCartId_Exception, InvalidQuantity_Exception,
	    NotEnoughItems_Exception, InvalidItemId_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("", itemIdView, 0);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartWhitespaceCartIdTest() throws InvalidCartId_Exception, InvalidQuantity_Exception,
	    NotEnoughItems_Exception, InvalidItemId_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart(" ", itemIdView, 0);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartTabCartIdTest() throws InvalidCartId_Exception, InvalidQuantity_Exception,
	    NotEnoughItems_Exception, InvalidItemId_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("\t", itemIdView, 0);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartNewlineCartIdTest() throws InvalidCartId_Exception, InvalidQuantity_Exception,
	    NotEnoughItems_Exception, InvalidItemId_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("\n", itemIdView, 0);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartNonAlphaCartIdTest() throws InvalidCartId_Exception, InvalidQuantity_Exception,
	    NotEnoughItems_Exception, InvalidItemId_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("#", itemIdView, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartNullItemIdTest() throws InvalidCartId_Exception, InvalidQuantity_Exception,
	    NotEnoughItems_Exception, InvalidItemId_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("goodcartid", null, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartNullItemPIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId(null);
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartEmptyItemPIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartWhitespaceItemPIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId(" ");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartTabItemPIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("\n");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartNewlineItemPIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("\n");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartNonAlphaItemPIdTest() throws InvalidCartId_Exception, InvalidQuantity_Exception,
	    NotEnoughItems_Exception, InvalidItemId_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("#");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartNullItemSIddTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId(null);
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartEmptyItemSIddTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("");
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartWhitespaceItemSIddTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId(" ");
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartTabItemSIddTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("\t");
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = InvalidQuantity_Exception.class)
    public void addToCartNegativeQtdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("goodcartid", itemIdView, -3);
    }

    @Test(expected = InvalidQuantity_Exception.class)
    public void addToCartZeroQtdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("sid");
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartNewlineItemSIddTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("pid");
	itemIdView.setSupplierId("\n");
	mediatorClient.addToCart("goodcartid", itemIdView, 0);
    }

    @Test(expected = NotEnoughItems_Exception.class)
    public void addToCartTooMuchQtdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
	    InvalidQuantity_Exception, NotEnoughItems_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("Animal01");
	itemIdView.setSupplierId("T63_Supplier1");
	mediatorClient.addToCart("goodcartid", itemIdView, 254);
    }

    // Valid tests
    // very incomplete tests

    @Test
    public void addToCartSingleItemMultipleSupTest() throws InvalidCartId_Exception, InvalidQuantity_Exception,
	    NotEnoughItems_Exception, InvalidItemId_Exception {
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId("Animal01");
	itemIdView.setSupplierId("T63_Supplier1");
	mediatorClient.addToCart("goodcartid", itemIdView, 1);
	CartView cartView = mediatorClient.listCarts().get(0);
	assertEquals("goodcartid", cartView.getCartId());
	assertEquals("Animal01", cartView.getItems().get(0));
    }

    // @Test
    // public void addToCartSingleItemSingleSup1Test()
    // throws InvalidCartId_Exception, InvalidQuantity_Exception,
    // NotEnoughItems_Exception {
    // mediatorClient.addToCart("Animal04", null, 0);
    // assertEquals(1, output.size());
    // }
    //
    // @Test
    // public void addToCartSingleItemSingleSup2Test()
    // throws InvalidCartId_Exception, InvalidQuantity_Exception,
    // NotEnoughItems_Exception {
    // mediatorClient.addToCart("Animal07", null, 0);
    // assertEquals(1, output.size());
    // }
}
