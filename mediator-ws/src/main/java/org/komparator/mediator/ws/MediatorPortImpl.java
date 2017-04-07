package org.komparator.mediator.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.jws.WebService;

import org.komparator.mediator.domain.Cart;
import org.komparator.mediator.domain.Item;
import org.komparator.mediator.domain.Mediator;
import org.komparator.mediator.domain.ShoppingResult;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

@WebService(endpointInterface = "org.komparator.mediator.ws.MediatorPortType", wsdlLocation = "mediator.wsdl", name = "MediatorWebService", portName = "MediatorPort", targetNamespace = "http://ws.mediator.komparator.org/", serviceName = "MediatorService")
public class MediatorPortImpl implements MediatorPortType {

    // end point manager
    private MediatorEndpointManager endpointManager;

    public MediatorPortImpl(MediatorEndpointManager endpointManager) {
	this.endpointManager = endpointManager;
    }

    // Main operations -------------------------------------------------------

    @Override
    public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
	// Arguments verification
	if (productId == null) {
	    throwInvalidItemId("Product identifier cannot be null!");
	}
	productId = productId.trim();
	if (productId.length() == 0) {
	    throwInvalidItemId("Product identifier cannot be empty or whitespace!");
	}
	if (Pattern.compile("[^a-zA-Z0-9]").matcher(productId).find()) {
	    throwInvalidItemId("Product identifier must be alpha numeric!");
	}
	try {
	    UDDINaming uddiNaming = endpointManager.getUddiNaming();
	    Collection<UDDIRecord> records = uddiNaming.listRecords("T63_Supplier%");
	    List<ItemView> result = new ArrayList<ItemView>();
	    for (UDDIRecord record : records) {
		SupplierClient client = new SupplierClient(uddiNaming.getUDDIUrl(), record.getOrgName());
		try {
		    Item item = new Item(client.getProduct(productId), record.getOrgName());
		    ItemView itemFromGet = newItemView(item);
		    for (int i = 0; i < result.size(); i++) {
			ItemView itemFromResult = result.get(i);
			if (itemFromResult.getPrice() > itemFromGet.getPrice()) {
			    result.add(i, itemFromGet);
			    break;
			}
		    }
		} catch (BadProductId_Exception e) {
		    throwInvalidItemId(e.getFaultInfo().getMessage());
		}
	    }
	    return result;
	} catch (UDDINamingException | SupplierClientException e) {
	    // continue;
	}
	return null;
    }

    @Override
    public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
	// Arguments verification
	if (descText == null) {
	    throwInvalidText("Search string cannot be null");
	}
	descText = descText.trim();
	if (descText.length() == 0) {
	    throwInvalidText("Search string cannot be empty or whitespace!");
	}
	try {
	    UDDINaming uddiNaming = endpointManager.getUddiNaming();
	    Collection<UDDIRecord> records = uddiNaming.listRecords("T63_Supplier%");
	    List<ItemView> result = new ArrayList<ItemView>();
	    for (UDDIRecord record : records) {
		SupplierClient client = new SupplierClient(uddiNaming.getUDDIUrl(), record.getOrgName());
		try {
		    List<ProductView> tempResult = client.searchProducts(descText);
		    for (ProductView tempProduct : tempResult) {
			Item item = new Item(tempProduct, record.getOrgName());
			ItemView itemFromSearch = newItemView(item);
			for (int i = 0; i < result.size(); i++) {
			    ItemView itemFromResult = result.get(i);
			    int compareValue = itemFromResult.getItemId().getProductId()
				    .compareToIgnoreCase(itemFromSearch.getItemId().getProductId());
			    if (compareValue > 0) {
				result.add(i, itemFromSearch);
				break;
			    } else if (compareValue == 0) {
				if (itemFromResult.getPrice() > itemFromSearch.getPrice()) {
				    result.add(i, itemFromSearch);
				    break;
				}
			    }
			}
		    }
		} catch (BadText_Exception e) {
		    throwInvalidText(e.getFaultInfo().getMessage());
		}
	    }
	    return result;
	} catch (UDDINamingException | SupplierClientException e) {
	    // continue;
	}
	return null;
    }

    @Override
    public ShoppingResultView buyCart(String cartId, String creditCardNr)
	    throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
	    InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
	// CartId verification
	if (cartId == null) {
	    throwInvalidCartId("Cart identifier cannot be null!");
	}
	cartId = cartId.trim();
	if (cartId.length() == 0) {
	    throwInvalidCartId("Cart identifier cannot be empty or whitespace!");
	}
	if (Pattern.compile("[^a-zA-Z0-9]").matcher(cartId).find()) {
	    throwInvalidCartId("Cart identifier must be alpha numeric!");
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
	    throwInvalidItemId("Item-Product identifier cannot be empty or whitespace!");
	}
	String sid = itemId.getSupplierId().trim();
	if (sid.length() == 0) {
	    throwInvalidItemId("Item-Supplier identifier cannot be empty or whitespace!");
	}
	if (Pattern.compile("[^a-zA-Z0-9]").matcher(itemId.getProductId()).find()) {
	    throwInvalidItemId("Item-Product identifier must be alpha numeric!");
	}
	if (itemQty <= 0) {
	    throwInvalidQuantity("Quantity cannot be zero or less!");
	}
	Cart cart = null;
	Mediator mediator = Mediator.getInstance();
	for (CartView cartView : listCarts()) {
	    if (cartView.getCartId().equals(cartId)) {
		cart = mediator.getCart(cartView.getCartId());
	    } else {
		mediator.addCart(new Cart(cartId));
		cart = mediator.getCart(cartId);
	    }
	}
	try {
	    UDDINaming uddiNaming = endpointManager.getUddiNaming();
	    SupplierClient client = new SupplierClient(uddiNaming.getUDDIUrl(), itemId.getSupplierId());
	    ProductView product = client.getProduct(itemId.getProductId());
	    if (cart.getItemById(itemId) != null) {
		itemQty += cart.getItemById(itemId).getQuantity();
	    }
	    if (itemQty < product.getQuantity()) {
		throwNotEnoughItems("The selected item does not have the desired quantity!");
	    }

	    cart.addItemToCart(itemId, product, itemQty);
	} catch (BadProductId_Exception e) {
	    throwInvalidItemId(e.getFaultInfo().getMessage());
	} catch (SupplierClientException e) {
	    // continue;
	}

    }
    // Auxiliary operations --------------------------------------------------

    @Override
    public void clear() {
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
	String pong = "";
	if (arg0 == null || arg0.trim().length() == 0) {
	    arg0 = "friend";
	}
	try {
	    UDDINaming uddiNaming = endpointManager.getUddiNaming();
	    Collection<UDDIRecord> records = uddiNaming.listRecords("T63_Supplier%");

	    for (UDDIRecord record : records) {
		SupplierClient client = new SupplierClient(uddiNaming.getUDDIUrl(), record.getOrgName());
		System.out.println("Invoke ping()...");
		pong += client.ping(arg0) + " /n ";
	    }
	    return pong;
	} catch (UDDINamingException | SupplierClientException e) {
	    pong = e.getClass().toString();
	}
	return pong;
    }

    @Override
    public List<ShoppingResultView> shopHistory() {
	Mediator mediator = Mediator.getInstance();
	List<ShoppingResultView> shoppingResultList = new ArrayList<ShoppingResultView>();
	for (String shoppingResultId : mediator.getShoppingResultsIds()) {
	    ShoppingResult shoppingResult = mediator.getShoppingResult(shoppingResultId);
	    ShoppingResultView shoppingResultView = newShoppingResultView(shoppingResult);
	    shoppingResultList.add(shoppingResultView);
	}
	return shoppingResultList;
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
	throw new EmptyCart_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwInvalidCartId(final String message) throws InvalidCartId_Exception {
	InvalidCartId faultInfo = new InvalidCartId();
	faultInfo.message = message;
	throw new InvalidCartId_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwInvalidCreditCard(final String message) throws InvalidCreditCard_Exception {
	InvalidCreditCard faultInfo = new InvalidCreditCard();
	faultInfo.message = message;
	throw new InvalidCreditCard_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwInvalidItemId(final String message) throws InvalidItemId_Exception {
	InvalidItemId faultInfo = new InvalidItemId();
	faultInfo.message = message;
	throw new InvalidItemId_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwInvalidQuantity(final String message) throws InvalidQuantity_Exception {
	InvalidQuantity faultInfo = new InvalidQuantity();
	faultInfo.message = message;
	throw new InvalidQuantity_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwInvalidText(final String message) throws InvalidText_Exception {
	InvalidText faultInfo = new InvalidText();
	faultInfo.message = message;
	throw new InvalidText_Exception(message, faultInfo);
    }

    /** Helper method to throw new BadProductId exception */
    private void throwNotEnoughItems(final String message) throws NotEnoughItems_Exception {
	NotEnoughItems faultInfo = new NotEnoughItems();
	faultInfo.message = message;
	throw new NotEnoughItems_Exception(message, faultInfo);
    }

}
