package org.komparator.mediator.ws.it;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

public class BaseIT {

    private static final String TEST_PROP_FILE = "/test.properties";
    protected static Properties testProps;

    protected static MediatorClient mediatorClient;
    protected static List<SupplierClient> supplierList = new ArrayList<SupplierClient>();

    protected static List<List<ItemView>> supplierInvList = new ArrayList<List<ItemView>>();
    private static List<String> supplierNames = new ArrayList<String>();

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
	} else {
	    mediatorClient = new MediatorClient(wsURL);
	}
	UDDINaming uddiNaming = new UDDINaming(uddiURL);
	Collection<UDDIRecord> records = uddiNaming.listRecords("T63_Supplier%");
	for (UDDIRecord record : records) {
	    SupplierClient client = new SupplierClient(uddiNaming.getUDDIUrl(), record.getOrgName());
	    supplierList.add(client);
	    supplierNames.add(record.getOrgName());
	    supplierInvList.add(new ArrayList<ItemView>());
	}

    }

    @AfterClass
    public static void cleanup() {
    }

    protected static ItemView productToItemView(ProductView product, String supplier) {
	ItemView itemView = new ItemView();
	itemView.setPrice(product.getPrice());
	itemView.setDesc(product.getDesc());
	ItemIdView itemIdView = new ItemIdView();
	itemIdView.setProductId(product.getId());
	itemIdView.setSupplierId(supplier);
	itemView.setItemId(itemIdView);
	return itemView;
    }

    protected static void populate()
	    throws BadProductId_Exception, BadProduct_Exception, UDDINamingException, SupplierClientException {
	// itens comuns
	for (int i = 0; i < supplierList.size(); i++) {

	    SupplierClient client = new SupplierClient(testProps.getProperty("uddi.url"), supplierNames.get(i));
	    {

		ProductView product = new ProductView();
		product.setId("Animal01");
		product.setDesc("Bird yellow");
		product.setPrice(new Random().nextInt(10) + 1);
		product.setQuantity(10);
		client.createProduct(product);
		supplierInvList.get(i).add(productToItemView(product, supplierNames.get(i)));
	    }
	    {
		ProductView product = new ProductView();
		product.setId("Animal02");
		product.setDesc("Guinea yellow pig");
		product.setPrice(new Random().nextInt(10) + 1);
		product.setQuantity(10);
		client.createProduct(product);
		supplierInvList.get(i).add(productToItemView(product, supplierNames.get(i)));
	    }
	    {
		ProductView product = new ProductView();
		product.setId("Animal03");
		product.setDesc("Yellow guinea pig");
		product.setPrice(new Random().nextInt(10) + 1);
		product.setQuantity(10);
		client.createProduct(product);
		supplierInvList.get(i).add(productToItemView(product, supplierNames.get(i)));
	    }

	}
	// itens sup1
	{
	    ProductView product = new ProductView();
	    product.setId("Animal04");
	    product.setDesc("Guinea pig");
	    product.setPrice(new Random().nextInt(10) + 1);
	    product.setQuantity(10);
	    supplierList.get(0).createProduct(product);
	    supplierInvList.get(0).add(productToItemView(product, supplierNames.get(0)));
	}
	{
	    ProductView product = new ProductView();
	    product.setId("Animal05");
	    product.setDesc("Guinea pig yellow");
	    product.setPrice(new Random().nextInt(10) + 1);
	    product.setQuantity(10);
	    supplierList.get(0).createProduct(product);
	    supplierInvList.get(0).add(productToItemView(product, supplierNames.get(0)));
	}
	{
	    ProductView product = new ProductView();
	    product.setId("Animal06");
	    product.setDesc("Red bird");
	    product.setPrice(new Random().nextInt(10) + 1);
	    product.setQuantity(10);
	    supplierList.get(0).createProduct(product);
	    supplierInvList.get(0).add(productToItemView(product, supplierNames.get(0)));
	}
	// itens sup2
	{
	    ProductView product = new ProductView();
	    product.setId("Animal07");
	    product.setDesc("Red pig");
	    product.setPrice(new Random().nextInt(10) + 1);
	    product.setQuantity(10);
	    supplierList.get(1).createProduct(product);
	    supplierInvList.get(1).add(productToItemView(product, supplierNames.get(1)));
	}
	{
	    ProductView product = new ProductView();
	    product.setId("Animal08");
	    product.setDesc("City mouse");
	    product.setPrice(new Random().nextInt(10) + 1);
	    product.setQuantity(10);
	    supplierList.get(1).createProduct(product);
	    supplierInvList.get(1).add(productToItemView(product, supplierNames.get(1)));
	}
	{
	    ProductView product = new ProductView();
	    product.setId("Animal09");
	    product.setDesc("Country mouse");
	    product.setPrice(new Random().nextInt(10) + 1);
	    product.setQuantity(10);
	    supplierList.get(1).createProduct(product);
	    supplierInvList.get(1).add(productToItemView(product, supplierNames.get(1)));
	}
    }
}
