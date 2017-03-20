package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;

/**
 * Test suite
 */
public class BuyProductIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		// clear remote service state before all tests
		client.clear();

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Y1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		client.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	// tests
	// assertEquals(expected, actual);

	// public String buyProduct(String productId, int quantity)
	// throws BadProductId_Exception, BadQuantity_Exception,
	// InsufficientQuantity_Exception {

	// bad input tests

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNullTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(null, 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductEmptyTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductWhitespaceTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(" ", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductTabTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\t", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNewlineTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\n", 5);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductNegativeQuantity()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1", -5);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductZeroQuantity()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1", 0);
	}

	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductTooMuchQuantity()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1", 100);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductUnknownProduct()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("GG", 10);
	}
	// main tests

	@Test
	public void buyProductMinQuantityTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		ProductView product = client.getProduct("X1");
		int inicialQuantity = product.getQuantity();
		client.buyProduct("X1", 1);
		product = client.getProduct("X1");
		assertEquals(inicialQuantity - 1, product.getQuantity());
	}

	@Test
	public void buyProductNormalQuantityTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		ProductView product = client.getProduct("Y1");
		int inicialQuantity = product.getQuantity();
		client.buyProduct("Y1", 5);
		product = client.getProduct("Y1");
		assertEquals(inicialQuantity - 5, product.getQuantity());
	}

	@Test
	public void buyProductMaxQuantityTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		ProductView product = client.getProduct("Z1");
		int inicialQuantity = product.getQuantity();
		client.buyProduct("Z1", 10);
		product = client.getProduct("Z1");
		assertEquals(inicialQuantity - 10, product.getQuantity());

	}
}
