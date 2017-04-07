package org.komparator.mediator.domain;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClientException;

public class Mediator {
    // Members ---------------------------------------------------------------

    /**
     * Map of existing carts. Uses concurrent hash table implementation
     * supporting full concurrency of retrievals and high expected concurrency
     * for updates.
     */
    private Map<String, Cart> carts = new ConcurrentHashMap<>();

    /**
     * Map of shoppingResults. Also uses concurrent hash table implementation.
     */
    private Map<String, ShoppingResult> shoppingResults = new ConcurrentHashMap<>();

    // Singleton -------------------------------------------------------------

    /* Private constructor prevents instantiation from other classes */
    private Mediator() {
    }

    /**
     * SingletonHolder is loaded on the first execution of
     * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
     * not before.
     */
    private static class SingletonHolder {
	private static final Mediator INSTANCE = new Mediator();
    }

    public static synchronized Mediator getInstance() {
	return SingletonHolder.INSTANCE;
    }

    // - - - - - - - - - - - - - - - - - - - - - -
    public void reset() {
	shoppingResults.clear();
	carts.clear();
    }

    public String generateShoppingResultId() {
	boolean valid = false;
	String shoppingResultId = null;
	int numChars = 5;
	while (!valid) {
	    char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
	    StringBuilder sb = new StringBuilder();
	    Random random = new Random();
	    for (int i = 0; i < numChars; i++) {
		char c = chars[random.nextInt(chars.length)];
		sb.append(c);
	    }
	    shoppingResultId = sb.toString();
	    if (!shoppingResults.containsKey(shoppingResultId)) {
		valid = true;
	    }
	}
	return shoppingResultId;
    }

    public boolean validateCreditCard(String ccNumber) throws CreditCardClientException {

	// FIXME @#%@#!RVWEG ^%#$^% #@$ %@#
	String uddiURL = null;
	String wsName = null;

	CreditCardClient ccc = new CreditCardClient(uddiURL, wsName);
	return ccc.validateNumber(ccNumber);
    }

    public Set<String> getCartsIds() {
	return carts.keySet();
    }

    public Cart getCart(String cartId) {
	return carts.get(cartId);
    }

    public Set<String> getShoppingResultsIds() {
	return shoppingResults.keySet();
    }

    public ShoppingResult getShoppingResult(String shoppingResultId) {
	return shoppingResults.get(shoppingResultId);
    }

    public void addCart(Cart cart) {
	carts.put(cart.getId(), cart);
    }
}
