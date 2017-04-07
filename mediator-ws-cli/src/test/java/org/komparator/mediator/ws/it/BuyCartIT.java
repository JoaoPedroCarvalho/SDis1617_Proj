package org.komparator.mediator.ws.it;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

/**
 * Test suite
 */
public class BuyCartIT extends BaseIT {
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
    public void buyCartNullCartIdTest()
	    throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = null;
	String ccnum = "1234567890123456";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void buyCartEmptyCartIdTest()
	    throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "";
	String ccnum = "1234567890123456";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void buyCartWhitespaceCartIdTest()
	    throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = " ";
	String ccnum = "1234567890123456";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void buyCartTabCartIdTest()
	    throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "\t";
	String ccnum = "1234567890123456";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void buyCartNewlineCartIdTest()
	    throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "\n";
	String ccnum = "1234567890123456";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void buyCartNonAlphaCartIdTest()
	    throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "#";
	String ccnum = "1234567890123456";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartCCNullTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "cartId";
	String ccnum = null;
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartCCEmptyTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "cartId";
	String ccnum = "";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartCCSpaceTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "cartId";
	String ccnum = " ";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartCCTAbTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "cartId";
	String ccnum = "\t";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartCCNewLineTest()
	    throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "cartId";
	String ccnum = "\n";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartCCNumVarTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "cartId";
	String ccnum = "123456789012345";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartCCNumVar2Test()
	    throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "cartId";
	String ccnum = "12345678901234567";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartCCLetterTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "cartId";
	String ccnum = "123456789012345a";
	mediatorClient.buyCart(cid, ccnum);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartCCNotAlphaTest()
	    throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	String cid = "cartId";
	String ccnum = "123456789012345#";
	mediatorClient.buyCart(cid, ccnum);
    }
}
