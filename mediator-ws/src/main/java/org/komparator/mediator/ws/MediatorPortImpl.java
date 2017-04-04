package org.komparator.mediator.ws;

import java.util.Collection;
import java.util.List;

import javax.jws.WebService;

import org.komparator.supplier.ws.cli.SupplierClient;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
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
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
	// TODO Auto-generated method stub
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
	// TODO Auto-generated method stub

    }
    // Auxiliary operations --------------------------------------------------

    @Override
    public void clear() {
	// TODO Auto-generated method stub
    }

    @Override
    public List<CartView> listCarts() {
	// TODO Auto-generated method stub
	return null;
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
		pong += client.ping(arg0) + " # ";
	    }
	    return pong;
	} catch (Exception e) {
	    // TODO: handle exception
	}
	return pong;
    }

    @Override
    public List<ShoppingResultView> shopHistory() {
	// TODO Auto-generated method stub
	return null;
    }

    // View helpers -----------------------------------------------------

    // private ShoppingResultView newShoppingResultView(ShoppingResult
    // shoppingResult) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // private ItemView newItemView(Item item) {
    // // TODO Auto-generated method stub
    //
    // }

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
