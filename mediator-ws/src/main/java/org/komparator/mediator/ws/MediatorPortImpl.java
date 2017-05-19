package org.komparator.mediator.ws;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.komparator.mediator.domain.Cart;
import org.komparator.mediator.domain.Item;
import org.komparator.mediator.domain.Mediator;
import org.komparator.mediator.domain.ShoppingResult;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

@WebService(endpointInterface = "org.komparator.mediator.ws.MediatorPortType", wsdlLocation = "mediator.wsdl", name = "MediatorWebService", portName = "MediatorPort", targetNamespace = "http://ws.mediator.komparator.org/", serviceName = "MediatorService")
@HandlerChain(file = "/mediator_handler-chain.xml")
public class MediatorPortImpl implements MediatorPortType {

    // end point manager
    private MediatorEndpointManager endpointManager;

    public MediatorPortImpl(MediatorEndpointManager endpointManager) {
	this.endpointManager = endpointManager;
    }

    @Resource
    private WebServiceContext webServiceContext;
    private String secMediatorUrl = "http://localhost:8072/mediator-ws/endpoint";

    // Main operations -------------------------------------------------------

    @Override
    public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {

	if (endpointManager.isVerbose()) {
	    System.out.println("- getItems( " + productId + " )");
	}
	// Arguments verification
	if (productId == null) {
	    throwInvalidItemId("Product identifier cannot be null!");
	}
	productId = productId.trim();
	if (productId.length() == 0) {
	    throwInvalidItemId("Product identifier cannot be empty or whitespace!");
	}
	if (Pattern.compile("[^a-zA-Z0-9]").matcher(productId).find()) {
	    throwInvalidItemId("Product identifier '" + productId + "' must be alpha numeric!");
	}
	try {
	    UDDINaming uddiNaming = endpointManager.getUddiNaming();
	    Collection<UDDIRecord> records = uddiNaming.listRecords("T63_Supplier%");
	    List<ItemView> result = new ArrayList<ItemView>();
	    for (UDDIRecord record : records) {
		SupplierClient client = new SupplierClient(uddiNaming.getUDDIUrl(), record.getOrgName());
		try {
		    ProductView product = client.getProduct(productId);
		    if (product == null) {
			continue;
		    }
		    String wsname = record.getOrgName();
		    Item item = new Item(product.getDesc(), product.getPrice(), productId, wsname);
		    ItemView itemFromGet = newItemView(item);
		    int i = 0;
		    while (i < result.size()) {
			ItemView itemFromResult = result.get(i);
			if (itemFromResult.getPrice() > itemFromGet.getPrice()) {
			    break;
			}
			i++;
		    }
		    result.add(i, itemFromGet);
		} catch (BadProductId_Exception e) {
		    throwInvalidItemId(e.getFaultInfo().getMessage());
		}
	    }
	    return result;
	} catch (UDDINamingException | SupplierClientException e) {
	    System.err.println(e.getClass());
	    // continue;
	}
	return null;
    }

    @Override
    public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
	if (endpointManager.isVerbose()) {
	    System.out.println("- searchItems( " + descText + " )");
	}
	// Arguments verification
	if (descText == null) {
	    throwInvalidText("Search string cannot be null");
	}
	descText = descText.trim();
	if (descText.length() == 0) {
	    throwInvalidText("Search string cannot be empty or whitespace!");
	}
	// Operation
	try {
	    List<ItemView> operationResult = new ArrayList<ItemView>();
	    UDDINaming uddiNaming = endpointManager.getUddiNaming();
	    Collection<UDDIRecord> supplierRecords = uddiNaming.listRecords("T63_Supplier%");
	    for (UDDIRecord supplier : supplierRecords) {
		String wsName = supplier.getOrgName();
		SupplierClient supplierClient = new SupplierClient(uddiNaming.getUDDIUrl(), wsName);
		try {
		    List<ProductView> supplierResponse = supplierClient.searchProducts(descText);
		    for (ProductView goodProduct : supplierResponse) {
			ItemView itemToSort = newItemView(
				new Item(goodProduct.getDesc(), goodProduct.getPrice(), goodProduct.getId(), wsName));
			// Sorted on insert
			int i = 0;
			while (i < operationResult.size()) {
			    ItemView itemOnResult = operationResult.get(i);
			    int compareValue = itemOnResult.getItemId().getProductId()
				    .compareToIgnoreCase(itemToSort.getItemId().getProductId());
			    if (compareValue > 0) {
				break;
			    } else if (compareValue == 0) {
				if (itemOnResult.getPrice() > itemToSort.getPrice()) {
				    break;
				}
			    }
			    i++;
			}
			operationResult.add(i, itemToSort);
		    }
		} catch (BadText_Exception e) {
		    throwInvalidText(e.getFaultInfo().getMessage());
		}
	    }

	    return operationResult;
	} catch (UDDINamingException | SupplierClientException e) {
	    System.err.println(e.getClass());
	    //
	}
	return null;
    }

    @Override
    public ShoppingResultView buyCart(String cartId, String creditCardNr)
	    throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	if (endpointManager.isVerbose()) {
	    System.out.println("- buyCart( " + cartId + " , " + creditCardNr + " )");
	}
	// CartId verification
	if (cartId == null) {
	    throwInvalidCartId("Cart identifier cannot be null!");
	}
	cartId = cartId.trim();
	if (cartId.length() == 0) {
	    throwInvalidCartId("Cart identifier cannot be empty or whitespace!");
	}
	if (Pattern.compile("[^a-zA-Z0-9]").matcher(cartId).find()) {
	    throwInvalidCartId("Cart identifier '" + cartId + "' must be alpha numeric!");
	}
	// CreditCard verification
	if (creditCardNr == null) {
	    throwInvalidCreditCard("CreditCard identifier cannot be null!");
	}
	creditCardNr = creditCardNr.trim();
	if (creditCardNr.length() == 0) {
	    throwInvalidCreditCard("CreditCard identifier cannot be empty or whitespace!");
	}
	if (creditCardNr.length() != 16) {
	    throwInvalidCreditCard("CreditCard identifier (" + creditCardNr + ") must have 16 digits!");
	}
	if (Pattern.compile("[^0-9]").matcher(creditCardNr).find()) {
	    throwInvalidCreditCard("CreditCard identifier (" + creditCardNr + ") must be numeric!");
	}
	// Operation
	Mediator mediator = Mediator.getInstance();
	Cart cart = mediator.getCart(cartId);
	if (cart == null) {
	    throwInvalidCartId("Cart provided '" + cartId + "' does not exist");
	}
	if (cart.getItems().isEmpty()) {
	    throwEmptyCart("Specified cart '" + cartId + "' is empty");
	}
	try {
	    if (!mediator.validateCreditCard(creditCardNr)) {
		throwInvalidCreditCard("Credit Card Number provided (" + creditCardNr + ") is not valid");
	    }
	} catch (CreditCardClientException e) {
	    throwInvalidCreditCard(e.getMessage());
	}
	ShoppingResult shoppingResult = new ShoppingResult();
	for (CartItemView cartItem : cart.getItems()) {
	    try {
		ItemIdView itemId = cartItem.getItem().getItemId();
		UDDINaming uddiNaming = endpointManager.getUddiNaming();
		SupplierClient client = new SupplierClient(uddiNaming.getUDDIUrl(), itemId.getSupplierId());
		client.buyProduct(itemId.getProductId(), cartItem.getQuantity());
		shoppingResult.addPurchasedItem(cartItem);
	    } catch (BadProductId_Exception | BadQuantity_Exception | InsufficientQuantity_Exception
		    | SupplierClientException e) {
		shoppingResult.addDroppedItem(cartItem);
	    }
	}
	shoppingResult.updateResult();
	shoppingResult.setShoppingResultId(mediator.generateShoppingResultId());
	mediator.addShoppingResult(shoppingResult);
	try {
	    if (endpointManager.getStatus().equals("primary")) {
		MediatorClient tempClient = new MediatorClient(secMediatorUrl);
		tempClient.updateShopHistory(shoppingResult.toView());
	    }
	} catch (Exception e) {
	    System.err.println("ERROR UPDATING shopping result");
	}
	return shoppingResult.toView();
    }

    @Override
    public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
	    InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
	if (endpointManager.isVerbose()) {
	    if (itemId != null) {
		System.out.println("- addToCart( " + cartId + " , " + itemId + " , " + itemId.getProductId() + " , "
			+ itemId.getSupplierId() + " , " + itemQty + " )-");
	    } else {
		System.out.println("- addToCart( " + cartId + " , " + itemId + " , " + itemQty + " )-");
	    }
	}

	// CartId verification
	if (cartId == null || cartId == "null") {
	    throwInvalidCartId("Cart identifier cannot be null!");
	}
	cartId = cartId.trim();
	if (cartId.length() == 0) {
	    throwInvalidCartId("Cart identifier cannot be empty or whitespace!");
	}
	if (Pattern.compile("[^a-zA-Z0-9]").matcher(cartId).find()) {
	    throwInvalidCartId("Cart identifier '" + cartId + "' must be alpha numeric!");
	}
	// ItemId verification
	if (itemId == null) {
	    throwInvalidItemId("Item identifier cannot be null!");
	}
	if (itemId.getProductId() == null) {
	    throwInvalidItemId("Item-Product identifier cannot be null!");
	}
	if (itemId.getSupplierId() == null) {
	    throwInvalidItemId("Item-Supplier identifier cannot be null!");
	}
	String pid = itemId.getProductId().trim();
	if (pid.length() == 0) {
	    throwInvalidItemId(
		    "Item-Product identifier '" + itemId.getProductId() + "' cannot be empty or whitespace!");
	}
	String sid = itemId.getSupplierId().trim();
	if (sid.length() == 0) {
	    throwInvalidItemId(
		    "Item-Supplier identifier '" + itemId.getSupplierId() + "' cannot be empty or whitespace!");
	}
	if (Pattern.compile("[^a-zA-Z0-9]").matcher(itemId.getProductId()).find()) {
	    throwInvalidItemId("Item-Product identifier '" + itemId.getProductId() + "' must be alpha numeric!");
	}
	if (itemQty <= 0) {
	    throwInvalidQuantity("Quantity (" + itemQty + ") cannot be zero or less!");
	}
	// Operation
	Cart cart = null;

	Mediator mediator = Mediator.getInstance();
	for (CartView cartView : listCarts()) {
	    if (cartView.getCartId().equals(cartId)) {
		cart = mediator.getCart(cartView.getCartId());
	    }
	}
	if (cart == null) {
	    mediator.addCart(new Cart(cartId));
	    cart = mediator.getCart(cartId);
	}

	try {
	    UDDINaming uddiNaming = endpointManager.getUddiNaming();
	    SupplierClient client = new SupplierClient(uddiNaming.getUDDIUrl(), itemId.getSupplierId());
	    ProductView product = client.getProduct(itemId.getProductId());
	    if (product == null) {
		throwInvalidItemId(
			"The item '" + itemId.getProductId() + "@" + itemId.getSupplierId() + "' does not exist");
	    }

	    if (cart.getItemById(itemId) != null) {
		itemQty += cart.getItemById(itemId).getQuantity();
	    }
	    if (itemQty > product.getQuantity()) {
		throwNotEnoughItems("The item '" + itemId.getProductId() + "@" + itemId.getSupplierId()
			+ "' does not have the desired quantity (" + itemQty + ")!");
	    }

	    cart.addItemToCart(itemId, product.getDesc(), product.getPrice(), product.getId(), itemQty);
	} catch (BadProductId_Exception e) {
	    throwInvalidItemId(e.getFaultInfo().getMessage());
	} catch (SupplierClientException e) {
	    // continue;
	}
	try {
	    if (endpointManager.getStatus().equals("primary")) {
		MediatorClient tempClient = new MediatorClient(secMediatorUrl);
		tempClient.updateCart(cart.toView());
	    }
	} catch (Exception e) {
	    System.err.println("ERROR UPDATING CART");
	}
    }
    // Auxiliary operations --------------------------------------------------

    @Override
    public void clear() {
	if (endpointManager.isVerbose()) {
	    System.out.println("- clear()");
	}
	Mediator.getInstance().reset();
	try {
	    UDDINaming uddiNaming = endpointManager.getUddiNaming();
	    Collection<UDDIRecord> records = uddiNaming.listRecords("T63_Supplier%");
	    for (UDDIRecord record : records) {
		SupplierClient client = new SupplierClient(uddiNaming.getUDDIUrl(), record.getOrgName());
		client.clear();
	    }
	} catch (UDDINamingException | SupplierClientException e) {
	    // continue;
	}

    }

    @Override
    public List<CartView> listCarts() {
	if (endpointManager.isVerbose()) {
	    System.out.println("- listCarts()");
	}
	Mediator mediator = Mediator.getInstance();
	List<CartView> cartList = new ArrayList<CartView>();
	for (String cartId : mediator.getCartsIds()) {
	    Cart cart = mediator.getCart(cartId);
	    CartView cv = newCartView(cart);
	    cartList.add(cv);
	}
	return cartList;
    }

    @Override
    public String ping(String arg0) {
	if (endpointManager.isVerbose()) {
	    System.out.println("- ping()");
	}
	String pong = "\n";
	if (arg0 == null || arg0.trim().length() == 0) {
	    arg0 = "friend";
	}
	try {
	    UDDINaming uddiNaming = endpointManager.getUddiNaming();
	    Collection<UDDIRecord> records = uddiNaming.listRecords("T63_Supplier%");

	    for (UDDIRecord record : records) {
		SupplierClient client = new SupplierClient(uddiNaming.getUDDIUrl(), record.getOrgName());
		System.out.println("Invoke ping()...");
		pong += client.ping(arg0) + " \n";
	    }
	    return pong;
	} catch (UDDINamingException | SupplierClientException e) {
	    pong = e.getClass().toString();
	}
	return pong;
    }

    @Override
    public List<ShoppingResultView> shopHistory() {
	if (endpointManager.isVerbose()) {
	    System.out.println("- shopHistory()");
	}
	Mediator mediator = Mediator.getInstance();
	List<ShoppingResultView> shoppingResultList = new ArrayList<ShoppingResultView>();
	for (String shoppingResultId : mediator.getShoppingResultsIds()) {
	    ShoppingResult shoppingResult = mediator.getShoppingResult(shoppingResultId);
	    ShoppingResultView shoppingResultView = newShoppingResultView(shoppingResult);
	    shoppingResultList.add(shoppingResultView);
	}
	return shoppingResultList;
    }

    @Override
    public void imAlive() {
	if (endpointManager.getStatus().equals("primary")) {
	    // is primary
	} else if (endpointManager.getStatus().equals("secondary")) {
	    LocalDateTime timeNow = LocalDateTime.now();
	    Mediator mediator = Mediator.getInstance();
	    mediator.setLastBreath(timeNow);
	    System.out.println("PRIMARY IS ALIVE");
	}
    }

    @Override
    public void updateShopHistory(ShoppingResultView shoppingResult) {
	Mediator mediator = Mediator.getInstance();
	mediator.refreshShopHistory(shoppingResult);
    }

    @Override
    public void updateCart(CartView cart) {
	Mediator mediator = Mediator.getInstance();
	mediator.refreshCart(cart);
    }

    // View helpers -----------------------------------------------------

    private ShoppingResultView newShoppingResultView(ShoppingResult shoppingResult) {
	return shoppingResult.toView();
    }

    private ItemView newItemView(Item item) {
	return item.toView();
    }

    private CartView newCartView(Cart cart) {
	return cart.toView();
    }

    // Exception helpers -----------------------------------------------------

    /** Helper method to throw new BadProductId exception */
    private void throwEmptyCart(final String message) throws EmptyCart_Exception {
	EmptyCart faultInfo = new EmptyCart();
	faultInfo.message = message;
	System.err.println(message);
	throw new EmptyCart_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwInvalidCartId(final String message) throws InvalidCartId_Exception {
	InvalidCartId faultInfo = new InvalidCartId();
	faultInfo.message = message;
	System.err.println(message);
	throw new InvalidCartId_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwInvalidCreditCard(final String message) throws InvalidCreditCard_Exception {
	InvalidCreditCard faultInfo = new InvalidCreditCard();
	faultInfo.message = message;
	System.err.println(message);
	throw new InvalidCreditCard_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwInvalidItemId(final String message) throws InvalidItemId_Exception {
	InvalidItemId faultInfo = new InvalidItemId();
	faultInfo.message = message;
	System.err.println(message);
	throw new InvalidItemId_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwInvalidQuantity(final String message) throws InvalidQuantity_Exception {
	InvalidQuantity faultInfo = new InvalidQuantity();
	faultInfo.message = message;
	System.err.println(message);
	throw new InvalidQuantity_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwInvalidText(final String message) throws InvalidText_Exception {
	InvalidText faultInfo = new InvalidText();
	faultInfo.message = message;
	System.err.println(message);
	throw new InvalidText_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwNotEnoughItems(final String message) throws NotEnoughItems_Exception {
	NotEnoughItems faultInfo = new NotEnoughItems();
	faultInfo.message = message;
	System.err.println(message);
	throw new NotEnoughItems_Exception(message, faultInfo);
    }
}
