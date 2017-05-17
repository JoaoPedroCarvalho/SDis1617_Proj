package org.komparator.supplier.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.komparator.supplier.domain.Product;
import org.komparator.supplier.domain.Purchase;
import org.komparator.supplier.domain.QuantityException;
import org.komparator.supplier.domain.Supplier;
import org.komparator.supplier.ws.handler.AuthServerHandler;

@WebService(endpointInterface = "org.komparator.supplier.ws.SupplierPortType", wsdlLocation = "supplier.wsdl", name = "SupplierWebService", portName = "SupplierPort", targetNamespace = "http://ws.supplier.komparator.org/", serviceName = "SupplierService")
@HandlerChain(file = "/supplier_handler-chain.xml")
public class SupplierPortImpl implements SupplierPortType {

    // end point manager
    private SupplierEndpointManager endpointManager;

    public SupplierPortImpl(SupplierEndpointManager endpointManager) {
	this.endpointManager = endpointManager;
    }

    @Resource
    private WebServiceContext webServiceContext;

    // Main operations -------------------------------------------------------

    @Override
    public ProductView getProduct(String productId) throws BadProductId_Exception {
	MessageContext messageContext = webServiceContext.getMessageContext();
	messageContext.put(AuthServerHandler.SUPPLIER_INDEX_PROPERTY, endpointManager.getWsName());
	Supplier supplier = Supplier.getInstance();
	if (endpointManager.isVerbose()) {
	    System.out.println("- getProduct( " + productId + " )");
	}
	// check product id
	if (productId == null)
	    throwBadProductId("Product identifier cannot be null!");
	productId = productId.trim();
	if (productId.length() == 0)
	    throwBadProductId("Product identifier cannot be empty or whitespace!");
	// retrieve product
	Product p = supplier.getProduct(productId);
	if (p != null) {
	    ProductView pv = newProductView(p);
	    // product found!
	    return pv;
	}
	// product not found
	return null;
    }

    @Override
    public List<ProductView> searchProducts(String descText) throws BadText_Exception {
	MessageContext messageContext = webServiceContext.getMessageContext();
	messageContext.put(AuthServerHandler.SUPPLIER_INDEX_PROPERTY, endpointManager.getWsName());
	Supplier supplier = Supplier.getInstance();
	if (endpointManager.isVerbose()) {
	    System.out.println("- searchProducts( " + descText + " )");
	}
	// Arguments verification
	if (descText == null) {
	    throwBadText("Search string cannot be null!");
	}
	descText = descText.trim();
	if (descText.length() == 0) {
	    throwBadText("Search string cannot be empty!");
	}
	// core
	List<ProductView> searchResult = new ArrayList<ProductView>();
	for (String productId : supplier.getProductsIDs()) {
	    // Iterate through list of products
	    Product product = supplier.getProduct(productId);
	    String description = product.getDescription();
	    if (description.contains(descText)) {
		// Located product with matching description
		searchResult.add(newProductView(product));
	    }
	}
	return searchResult;
    }

    @Override
    public String buyProduct(String productId, int quantity)
	    throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
	MessageContext messageContext = webServiceContext.getMessageContext();
	messageContext.put(AuthServerHandler.SUPPLIER_INDEX_PROPERTY, endpointManager.getWsName());
	Supplier supplier = Supplier.getInstance();
	if (endpointManager.isVerbose()) {
	    System.out.println("- createProduct( " + productId + " , " + quantity + " )");
	}
	// Arguments verification
	if (productId == null) {
	    throwBadProductId("Product identifier cannot be null!");
	}
	productId = productId.trim();
	if (productId.length() == 0) {
	    throwBadProductId("Product identifier cannot be empty or whitespace!");
	}
	if (Pattern.compile("[^a-zA-Z0-9]").matcher(productId).find()) {
	    throwBadProductId("Product identifier '" + productId + "' must be alpha numeric!");
	}
	if (quantity <= 0) {
	    throwBadQuantity("Quantity cannot be zero or less!");
	}
	// core
	Product product = supplier.getProduct(productId);
	if (product == null) {
	    // Checks if PId exists
	    throwBadProductId("Product identifier provided '" + productId + "' does not exist!");
	}
	if (product.getQuantity() < quantity) {
	    // Check if exists enough
	    throwInsufficientQuantity(
		    "Not enough quantity (" + quantity + ") of selected product '" + productId + "'!");
	}
	try {
	    String purchaseId = supplier.buyProduct(productId, quantity);
	    return purchaseId;
	} catch (QuantityException e) {
	    throwInsufficientQuantity(
		    "Not enough quantity (" + quantity + ") of selected product '" + productId + "'!");
	}
	return null;
    }

    // Auxiliary operations --------------------------------------------------

    @Override
    public String ping(String name) {
	if (endpointManager.isVerbose()) {
	    System.out.println("- ping()");
	}
	MessageContext messageContext = webServiceContext.getMessageContext();
	messageContext.put(AuthServerHandler.SUPPLIER_INDEX_PROPERTY, endpointManager.getWsName());
	if (name == null || name.trim().length() == 0)
	    name = "friend";

	String wsName = endpointManager.getWsName();

	StringBuilder builder = new StringBuilder();
	builder.append("Hello ").append(name);
	builder.append(" from ").append(wsName);
	return builder.toString();
    }

    @Override
    public void clear() {
	MessageContext messageContext = webServiceContext.getMessageContext();
	messageContext.put(AuthServerHandler.SUPPLIER_INDEX_PROPERTY, endpointManager.getWsName());
	if (endpointManager.isVerbose()) {
	    System.out.println("- clear()");
	}
	Supplier.getInstance().reset();
    }

    @Override
    public void createProduct(ProductView productToCreate) throws BadProductId_Exception, BadProduct_Exception {
	MessageContext messageContext = webServiceContext.getMessageContext();
	messageContext.put(AuthServerHandler.SUPPLIER_INDEX_PROPERTY, endpointManager.getWsName());
	if (endpointManager.isVerbose()) {
	    if (productToCreate == null) {
		System.out.println("- createProduct( " + productToCreate + " )");
	    } else {
		System.out.println("- createProduct( " + productToCreate + " , " + productToCreate.getId() + " , "
			+ productToCreate.getDesc() + " , " + productToCreate.getPrice() + " , "
			+ productToCreate.getQuantity() + " )");
	    }
	}
	// check null
	if (productToCreate == null) {
	    throwBadProduct("Product view cannot be null!");
	}
	// check id
	String productId = productToCreate.getId();
	if (productId == null)
	    throwBadProductId("Product identifier cannot be null!");
	productId = productId.trim();
	if (productId.length() == 0)
	    throwBadProductId("Product identifier cannot be empty or whitespace!");
	// check description
	String productDesc = productToCreate.getDesc();
	if (productDesc == null)
	    productDesc = "";
	// check quantity
	int quantity = productToCreate.getQuantity();
	if (quantity <= 0) {
	    throwBadProduct("Quantity (" + quantity + ")must be a positive number!");
	}
	// check price
	int price = productToCreate.getPrice();
	if (price <= 0) {
	    throwBadProduct("Price (" + price + ") must be a positive number!");
	}
	// create new product
	Supplier s = Supplier.getInstance();
	s.registerProduct(productId, productDesc, quantity, price);
    }

    @Override
    public List<ProductView> listProducts() {
	MessageContext messageContext = webServiceContext.getMessageContext();
	messageContext.put(AuthServerHandler.SUPPLIER_INDEX_PROPERTY, endpointManager.getWsName());
	Supplier supplier = Supplier.getInstance();
	if (endpointManager.isVerbose()) {
	    System.out.println("- listProducts()");
	}
	List<ProductView> pvs = new ArrayList<ProductView>();
	for (String pid : supplier.getProductsIDs()) {
	    Product p = supplier.getProduct(pid);
	    ProductView pv = newProductView(p);
	    pvs.add(pv);
	}
	return pvs;
    }

    @Override
    public List<PurchaseView> listPurchases() {
	MessageContext messageContext = webServiceContext.getMessageContext();
	messageContext.put(AuthServerHandler.SUPPLIER_INDEX_PROPERTY, endpointManager.getWsName());
	Supplier supplier = Supplier.getInstance();
	if (endpointManager.isVerbose()) {
	    System.out.println("- listPurchases()");
	}
	List<PurchaseView> pvs = new ArrayList<PurchaseView>();
	for (String pid : supplier.getPurchasesIDs()) {
	    Purchase p = supplier.getPurchase(pid);
	    PurchaseView pv = newPurchaseView(p);
	    pvs.add(pv);
	}
	return pvs;
    }

    // View helpers ----------------------------------------------------------

    private ProductView newProductView(Product product) {
	ProductView view = new ProductView();
	view.setId(product.getId());
	view.setDesc(product.getDescription());
	view.setQuantity(product.getQuantity());
	view.setPrice(product.getPrice());
	return view;
    }

    private PurchaseView newPurchaseView(Purchase purchase) {
	PurchaseView view = new PurchaseView();
	view.setId(purchase.getPurchaseId());
	view.setProductId(purchase.getProductId());
	view.setQuantity(purchase.getQuantity());
	view.setUnitPrice(purchase.getUnitPrice());
	return view;
    }

    // Exception helpers -----------------------------------------------------

    /** Helper method to throw new BadProductId exception */
    private void throwBadProductId(final String message) throws BadProductId_Exception {
	BadProductId faultInfo = new BadProductId();
	faultInfo.message = message;
	System.err.println(message);
	throw new BadProductId_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProduct exception */
    private void throwBadProduct(final String message) throws BadProduct_Exception {
	BadProduct faultInfo = new BadProduct();
	faultInfo.message = message;
	System.err.println(message);
	throw new BadProduct_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadText exception */
    private void throwBadText(final String message) throws BadText_Exception {
	BadText faultInfo = new BadText();
	faultInfo.message = message;
	System.err.println(message);
	throw new BadText_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadQuantity exception */
    private void throwBadQuantity(final String message) throws BadQuantity_Exception {
	BadQuantity faultInfo = new BadQuantity();
	faultInfo.message = message;
	System.err.println(message);
	throw new BadQuantity_Exception(message, faultInfo);
    }

    /** Helper method to throw new InsufficientQuantity exception */
    private void throwInsufficientQuantity(final String message) throws InsufficientQuantity_Exception {
	InsufficientQuantity faultInfo = new InsufficientQuantity();
	faultInfo.message = message;
	System.err.println(message);
	throw new InsufficientQuantity_Exception(message, faultInfo);
    }
}