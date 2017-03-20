package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.ProductView;

/**
 * Test suite
 */
public class SearchProductsIT extends BaseIT {

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
			product.setDesc("Basketball red");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer ball");
			product.setPrice(30);
			product.setQuantity(30);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z4");
			product.setDesc("Red Soccer ball");
			product.setPrice(30);
			product.setQuantity(30);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z5");
			product.setDesc("Soccer red ball");
			product.setPrice(30);
			product.setQuantity(30);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z6");
			product.setDesc("Soccer ball red");
			product.setPrice(30);
			product.setQuantity(30);
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

	// public List<ProductView> searchProducts(String descText) throws
	// BadText_Exception

	// bad input tests

	@Test(expected = BadText_Exception.class)
	public void searchProductsNullTest() throws BadText_Exception {
		client.searchProducts(null);
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsEmptyTest() throws BadText_Exception {
		client.searchProducts("");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsWhitespaceTest() throws BadText_Exception {
		client.searchProducts(" ");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsTabTest() throws BadText_Exception {
		client.searchProducts("\t");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsNewlineTest() throws BadText_Exception {
		client.searchProducts("\n");
	}

	// main tests

	@Test
	public void searchProductsThatExistSingleTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("Basketball");
		assertEquals("X1", productsList.get(0).getId());
		assertEquals(10, productsList.get(0).getPrice());
		assertEquals(10, productsList.get(0).getQuantity());
		assertEquals("Basketball red", productsList.get(0).getDesc());
	}

	@Test
	public void searchProductsThatExistMultipleTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("ball");
		assertEquals(5, productsList.size());
		
		assertEquals("X1", productsList.get(0).getId());
		assertEquals(10, productsList.get(0).getPrice());
		assertEquals(10, productsList.get(0).getQuantity());
		assertEquals("Basketball red", productsList.get(0).getDesc());

		assertEquals("Z3", productsList.get(1).getId());
		assertEquals(30, productsList.get(1).getPrice());
		assertEquals(30, productsList.get(1).getQuantity());
		assertEquals("Soccer ball", productsList.get(1).getDesc());
		
		assertEquals("Z4", productsList.get(2).getId());
		assertEquals(30, productsList.get(2).getPrice());
		assertEquals(30, productsList.get(2).getQuantity());
		assertEquals("Red soccer ball", productsList.get(2).getDesc());

		assertEquals("Z5", productsList.get(3).getId());
		assertEquals(30, productsList.get(3).getPrice());
		assertEquals(30, productsList.get(3).getQuantity());
		assertEquals("Soccer red ball", productsList.get(3).getDesc());

		assertEquals("Z6", productsList.get(4).getId());
		assertEquals(30, productsList.get(4).getPrice());
		assertEquals(30, productsList.get(4).getQuantity());
		assertEquals("Soccer ball red", productsList.get(4).getDesc());
	}

	@Test
	public void searchProductsThatDontExistSingleCaseSensitiveTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("SoCcEr");
		assertTrue(productsList.isEmpty());
	}

	@Test
	public void searchProductsThatExistSingleCaseSensitiveTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("soccer");
		assertEquals(1, productsList.size());

		assertEquals("Z4", productsList.get(0).getId());
		assertEquals(30, productsList.get(0).getPrice());
		assertEquals(30, productsList.get(0).getQuantity());
		assertEquals("Red soccer ball", productsList.get(0).getDesc());		
	}

	@Test
	public void searchProductsThatExistMultipleCaseSensitiveTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("Soccer");
		assertEquals(3, productsList.size());

		assertEquals("Z3", productsList.get(0).getId());
		assertEquals(30, productsList.get(0).getPrice());
		assertEquals(30, productsList.get(0).getQuantity());
		assertEquals("Soccer ball", productsList.get(0).getDesc());
		
		assertEquals("Z5", productsList.get(1).getId());
		assertEquals(30, productsList.get(1).getPrice());
		assertEquals(30, productsList.get(1).getQuantity());
		assertEquals("Soccer red ball", productsList.get(1).getDesc());

		assertEquals("Z6", productsList.get(2).getId());
		assertEquals(30, productsList.get(2).getPrice());
		assertEquals(30, productsList.get(2).getQuantity());
		assertEquals("Soccer ball red", productsList.get(2).getDesc());
	}

	@Test
	public void searchProductsReversedTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("ball soccer");
		assertTrue(productsList.isEmpty());
	}

	@Test
	public void searchProductsMultipleSpacingTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("Soccer  ball");
		assertTrue(productsList.isEmpty());
	}

	@Test
	public void searchProductsThatDoesNotExistTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("GolfBall");
		assertTrue(productsList.isEmpty());
	}

}
