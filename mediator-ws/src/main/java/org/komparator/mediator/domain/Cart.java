package org.komparator.mediator.domain;

import java.util.ArrayList;
import java.util.List;

import org.komparator.mediator.ws.CartItemView;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.ItemIdView;

public class Cart {

    private String cartId;
    private List<CartItemView> cartItems;

    public Cart(String cartId) {
	this.cartId = cartId;
	this.cartItems = new ArrayList<CartItemView>();
    }

    public void addItemToCart(ItemIdView itemId, String desc, int price, String pid, int quantity) {
	for (CartItemView cartItemView : cartItems) {
	    if (cartItemView.getItem().getItemId().getProductId().equals(itemId.getProductId())
		    && cartItemView.getItem().getItemId().getSupplierId().equals(itemId.getSupplierId())) {
		cartItemView.setQuantity(quantity);
		return;
	    }
	}
	CartItemView cartItemView = new CartItemView();
	cartItemView.setItem(new Item(desc, price, pid, itemId.getSupplierId()).toView());
	cartItemView.setQuantity(quantity);
	cartItems.add(cartItemView);
    }

    public String getId() {
	return this.cartId;
    }

    public List<CartItemView> getItems() {
	return this.cartItems;
    }

    public CartView toView() {
	CartView cartView = new CartView();
	cartView.setCartId(this.getId());
	cartView.getItems().addAll(this.getItems());
	return cartView;
    }

    public CartItemView getItemById(ItemIdView itemId) {
	for (CartItemView cartItemView : cartItems) {
	    ItemIdView cartItemId = cartItemView.getItem().getItemId();
	    if (cartItemId.getProductId().equals(itemId.getProductId())
		    && cartItemId.getSupplierId().equals(itemId.getSupplierId())) {
		return cartItemView;
	    }
	}
	return null;
    }

}
