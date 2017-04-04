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

		// Populate supplier
		{
			ProductView product = new ProductView();
			product.setId("Animal01");
			product.setDesc("Bird yellow");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Animal02");
			product.setDesc("Guinea pig");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Animal03");
			product.setDesc("Yellow guinea pig");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Animal04");
			product.setDesc("Guinea yellow pig");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Animal05");
			product.setDesc("Guinea pig yellow");
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

	// Empty output tests
	@Test
	public void searchProductsThatDoesNotExistTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("Fish");
		assertTrue(productsList.isEmpty());
	}

	@Test
	public void searchProductsMultipleSpacingTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("Guinea  pig");
		assertTrue(productsList.isEmpty());
	}

	@Test
	public void searchProductsReversedTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("pig guinea");
		assertTrue(productsList.isEmpty());
	}

	@Test
	public void searchProductsThatExistSingleCaseNotSensitiveTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("GuInEa");
		assertTrue(productsList.isEmpty());
	}

	// Valid input tests
	@Test
	public void searchProductsThatExistSingleTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("Bird");
		assertEquals("Animal01", productsList.get(0).getId());
		assertEquals("Bird yellow", productsList.get(0).getDesc());
	}

	@Test
	public void searchProductsThatExistMultipleTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("yellow");
		assertEquals(3, productsList.size());

		assertEquals("Animal04", productsList.get(0).getId());
		assertEquals("Guinea yellow pig", productsList.get(0).getDesc());

		assertEquals("Animal05", productsList.get(1).getId());
		assertEquals("Guinea pig yellow", productsList.get(1).getDesc());
		
		assertEquals("Animal01", productsList.get(2).getId());
		assertEquals("Bird yellow", productsList.get(2).getDesc());
	}

	@Test
	public void searchProductsThatExistSingleCaseSensitiveTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("guinea");
		assertEquals(1, productsList.size());

		assertEquals("Animal03", productsList.get(0).getId());
		assertEquals("Yellow guinea pig", productsList.get(0).getDesc());
	}

	@Test
	public void searchProductsThatExistMultipleCaseSensitiveTest() throws BadText_Exception {
		List<ProductView> productsList = client.searchProducts("Guinea");
		assertEquals(3, productsList.size());

		assertEquals("Animal04", productsList.get(0).getId());
		assertEquals("Guinea yellow pig", productsList.get(0).getDesc());

		assertEquals("Animal05", productsList.get(1).getId());
		assertEquals("Guinea pig yellow", productsList.get(1).getDesc());

		assertEquals("Animal02", productsList.get(2).getId());
		assertEquals("Guinea pig", productsList.get(2).getDesc());
	}
}
