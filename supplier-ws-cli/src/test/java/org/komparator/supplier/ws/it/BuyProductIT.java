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

		// Populate supplier
		{
			ProductView product = new ProductView();
			product.setId("Animal01");
			product.setDesc("Bird");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Animal02");
			product.setDesc("Fish");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Animal03");
			product.setDesc("Turtoise");
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

	// Tests

	// Invalid input tests
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

	@Test(expected = BadProductId_Exception.class)
	public void buyProductSpaceTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Animal 01", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductSymbol1Test()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Animal%01", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductSymbol2Test()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Animal.01", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductSymbol3Test()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Animal,01", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductSymbol4Test()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Animal;01", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductSymbol5Test()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Animal:01", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductSymbol6Test()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Animal-01", 5);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductSymbol7Test()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Animal_01", 5);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductNegativeQuantity()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Animal01", -5);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductZeroQuantity()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Animal01", 0);
	}

	// Wrong input tests
	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductTooMuchQuantity()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Animal01", 100);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductUnknownProduct()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Planta01", 10);
	}

	// Valid input tests
	@Test
	public void buyProductMinQuantityTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		ProductView product = client.getProduct("Animal01");
		int inicialQuantity = product.getQuantity();
		client.buyProduct("Animal01", 1);
		product = client.getProduct("Animal01");
		assertEquals(inicialQuantity - 1, product.getQuantity());
	}

	@Test
	public void buyProductNormalQuantityTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		ProductView product = client.getProduct("Animal02");
		int inicialQuantity = product.getQuantity();
		client.buyProduct("Animal02", 5);
		product = client.getProduct("Animal02");
		assertEquals(inicialQuantity - 5, product.getQuantity());
	}

	@Test
	public void buyProductMaxQuantityTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		ProductView product = client.getProduct("Animal03");
		int inicialQuantity = product.getQuantity();
		client.buyProduct("Animal03", 10);
		product = client.getProduct("Animal03");
		assertEquals(inicialQuantity - 10, product.getQuantity());

	}
}
