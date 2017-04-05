package org.komparator.mediator.domain;

import java.util.ArrayList;
import java.util.List;

import org.komparator.mediator.ws.CartItemView;
import org.komparator.mediator.ws.CartView;

public class Cart {

    private String cartId;
    private List<CartItemView> cartItems;

    public Cart(String cartId) {
	this.cartId = cartId;
	this.cartItems = new ArrayList<CartItemView>();
    }

    public void addItemToCart(Item item, int quantity) {
	for (CartItemView cartItemView : cartItems) {
	    if (cartItemView.getItem().getItemId() == item.getItemId()) {
		cartItemView.setQuantity(cartItemView.getQuantity() + quantity);
		// verificacao feita no portImpl
		return;
	    }
	}
	CartItemView cartItemView = new CartItemView();
	cartItemView.setItem(item.toView());
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

}
