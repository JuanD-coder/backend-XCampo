package com.rojas.dev.XCampo.service.ServiceImp;

import com.rojas.dev.XCampo.dto.CartItemDTO;
import com.rojas.dev.XCampo.dto.GetShoppingCartDTO;
import com.rojas.dev.XCampo.dto.ResponseCartItemDTO;
import com.rojas.dev.XCampo.dto.ShoppingCartDTO;
import com.rojas.dev.XCampo.entity.CartItem;
import com.rojas.dev.XCampo.entity.Client;
import com.rojas.dev.XCampo.entity.Shopping_cart;
import com.rojas.dev.XCampo.exception.EntityNotFoundException;
import com.rojas.dev.XCampo.exception.InvalidDataException;
import com.rojas.dev.XCampo.repository.CartItemRepository;
import com.rojas.dev.XCampo.repository.ShoppingCartRepository;
import com.rojas.dev.XCampo.service.Interface.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShoppingCarServiceImp implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCarRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ClientServiceImp clientServiceImp;

    @Override
    public ResponseCartItemDTO createShoppingCart(ShoppingCartDTO shoppingCart) {
        var client = clientServiceImp.findClientById(shoppingCart.getClientId());
        Shopping_cart existingCart = findExistingCart(client);

        if (existingCart != null) return response(existingCart);


        Shopping_cart addShoppingCart = new Shopping_cart();
        var total = totalEarnings(addShoppingCart.getId_cart());
        addShoppingCart.setClient(client);
        addShoppingCart.setDateAdded(shoppingCart.getDateAdded());
        addShoppingCart.setStatus(shoppingCart.isStatus());
        addShoppingCart.setTotalEarnings(total);

        Shopping_cart newShoppingCart = shoppingCarRepository.save(addShoppingCart);

        return response(newShoppingCart);
    }

    public Shopping_cart addItemToCart(Long cartIds, Long itemId) {
        Shopping_cart cart = findByIdShoppingCard(cartIds);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + itemId));

        cart.setItems((Set<CartItem>) item);
        item.setCart(cart);
        cart.getItems().add(item);

        updateCartTotal(cart);

        return shoppingCarRepository.save(cart);
    }

    @Override
    public void deleteProduct(Long idShoppingCar) {
        Shopping_cart entity = findByIdShoppingCard(idShoppingCar);
        clientServiceImp.existsClient(entity.getClient().getId_client());
        shoppingCarRepository.deleteById(idShoppingCar);
    }

    @Override
    public void updateState(Long idShoppingCar, boolean state) {
        Shopping_cart entity = findByIdShoppingCard(idShoppingCar);
        entity.setStatus(state);
        entity.setTotalEarnings(totalEarnings(idShoppingCar));
        shoppingCarRepository.save(entity);
    }

    private void updateCartTotal(Shopping_cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
        cart.setTotalEarnings((long) total);
    }

    @Override
    public Shopping_cart findByIdShoppingCard(Long id) {
        return shoppingCarRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Shopping cart not found with ID: " + id));
    }

    @Override
    public List<GetShoppingCartDTO> listAllProductsShoppingCart(Long idClient) {
        return shoppingCarRepository.findByClientId(idClient).stream()
                .map(this::convertToShoppingCartDTO)
                .toList();
    }

    @Override
    public ResponseEntity<?> getIdCartByIdUser(Long idUser) {
        try {
            Long response = shoppingCarRepository.getIdCartByIdUser(idUser);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
        }
    }

    // Convertir en un dto el carrito
    public GetShoppingCartDTO convertToShoppingCartDTO(Shopping_cart shoppingCart) {
        var cartItemDTOList = cartItemRepository.findByIdShoppingCart(shoppingCart.getId_cart()).stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toSet());

        return new GetShoppingCartDTO(
                shoppingCart.getId_cart(),
                shoppingCart.getClient().getId_client(),
                shoppingCart.getClient().getName(),
                shoppingCart.isStatus(),
                shoppingCart.getDateAdded(),
                shoppingCart.getTotalEarnings(),
                cartItemDTOList
        );
    }

    public ResponseCartItemDTO response (Shopping_cart shoppingCart) {
        Long items = cartItemRepository.getItemsTotal(shoppingCart);

        return new ResponseCartItemDTO(
                shoppingCart.getId_cart(),
                shoppingCart.getClient(),
                items,
                shoppingCart.getDateAdded(),
                shoppingCart.getTotalEarnings(),
                shoppingCart.getOrder()
        )
                ;
    }

    /*public GetShoppingCartDTO convertToShoppingCartDTOFilter(Shopping_cart shoppingCart, Set<CartItem> filteredItems) {
        var itemsDTO = filteredItems.stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toSet());

        var client = shoppingCart.getClient();

        return new GetShoppingCartDTO(
                shoppingCart.getId_cart(),
                client.getId_client(),
                client.getName(),
                shoppingCart.isStatus(),
                shoppingCart.getDateAdded(),
                shoppingCart.getTotalEarnings(),
                itemsDTO
        );
    }*/

    // Convertir en un DTO los items del carrito
    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        return new CartItemDTO(
                cartItem.getId_cart_item(),
                cartItem.getProduct().getId_product(),
                cartItem.getQuantity(),
                cartItem.getUnitPrice()
        );
    }

    private Shopping_cart findExistingCart(Client client) {
        List<Shopping_cart> existingCarts = shoppingCarRepository.findStatusFalse(client.getId_client());

        if (existingCarts.size() > 1) {
            throw new InvalidDataException("Multiple carts with 'false' status found for this client");
        }

        return existingCarts.isEmpty() ? null : existingCarts.get(0);
    }

    public Long totalEarnings(Long IdCart) {
        return cartItemRepository.findByIdShoppingCart(IdCart).stream()
                .mapToLong(CartItem::getUnitPrice)
                .sum();
    }

}
