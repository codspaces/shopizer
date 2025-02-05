package com.salesmanager.shop.store.api.v1.shoppingCart;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.shoppingcart.PersistableShoppingCartItem;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCart;
import com.salesmanager.shop.store.api.exception.OperationNotAllowedException;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.store.api.exception.ServiceRuntimeException;
import com.salesmanager.shop.store.controller.customer.facade.v1.CustomerFacade;
import com.salesmanager.shop.store.controller.shoppingCart.facade.ShoppingCartFacade;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1")
@Tag(tags = { "Shopping cart api" })
@SwaggerDefinition(tags = {
		@Tag(name = "Shopping cart resource", description = "Add, remove and retrieve shopping carts") })
public class ShoppingCartApi {

	@Inject
	private ShoppingCartFacade shoppingCartFacade;

	@Inject
	private com.salesmanager.shop.store.controller.shoppingCart.facade.v1.ShoppingCartFacade shoppingCartFacadev1;

	@Inject
	private CustomerService customerService;

	@Autowired
	private CustomerFacade customerFacadev1;
	
	@Autowired
	private com.salesmanager.shop.store.controller.customer.facade.CustomerFacade customerFacade;

	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCartApi.class);

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/cart")
	@Operation(httpMethod = "POST", summary = "Add product to shopping cart when no cart exists, this will create a new cart id", description = "No customer ID in scope. Add to cart for non authenticated users, as simple as {\"product\":1232,\"quantity\":1}")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody ReadableShoppingCart addToCart(
			@Valid @RequestBody PersistableShoppingCartItem shoppingCartItem,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		return shoppingCartFacade.addToCart(shoppingCartItem, merchantStore, language);
	}

	@PutMapping(value = "/cart/{code}")
	@Operation(httpMethod = "PUT", summary = "Add to an existing shopping cart or modify an item quantity", description = "No customer ID in scope. Modify cart for non authenticated users, as simple as {\"product\":1232,\"quantity\":0} for instance will remove item 1234 from cart")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ResponseEntity<ReadableShoppingCart> modifyCart(
			@PathVariable String code,
			@Valid @RequestBody PersistableShoppingCartItem shoppingCartItem, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, 
			HttpServletResponse response) {

		try {
			ReadableShoppingCart cart = shoppingCartFacade.modifyCart(code, shoppingCartItem, merchantStore, language);

			if (cart == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(cart, HttpStatus.CREATED);

		} catch (Exception e) {
			if(e instanceof ResourceNotFoundException exception) {
				throw exception;
			} else {
				throw new ServiceRuntimeException(e);
			}

		} 
	}
	

	@PostMapping(value = "/cart/{code}/promo/{promo}")
	@Operation(httpMethod = "POST", summary = "Add promo / coupon to an existing cart")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ResponseEntity<ReadableShoppingCart> modifyCart(
			@PathVariable String code,//shopping cart code
			@PathVariable String promo,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, 
			HttpServletResponse response) {

		try {
			ReadableShoppingCart cart = shoppingCartFacade.modifyCart(code, promo, merchantStore, language);

			if (cart == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(cart, HttpStatus.CREATED);

		} catch (Exception e) {
			if(e instanceof ResourceNotFoundException exception) {
				throw exception;
			} else {
				throw new ServiceRuntimeException(e);
			}

		} 
	}


	@PostMapping(value = "/cart/{code}/multi", consumes = { "application/json" }, produces = { "application/json" })
	@Operation(httpMethod = "POST", summary = "Add to an existing shopping cart or modify an item quantity", description = "No customer ID in scope. Modify cart for non authenticated users, as simple as {\"product\":1232,\"quantity\":0} for instance will remove item 1234 from cart")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ResponseEntity<ReadableShoppingCart> modifyCart(
			@PathVariable String code,
			@Valid @RequestBody PersistableShoppingCartItem[] shoppingCartItems, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		try {
			ReadableShoppingCart cart = shoppingCartFacade.modifyCartMulti(code, Arrays.asList(shoppingCartItems),
					merchantStore, language);

			return new ResponseEntity<>(cart, HttpStatus.CREATED);

		} catch (Exception e) {
			if(e instanceof ResourceNotFoundException exception) {
				throw exception;
			} else {
				throw new ServiceRuntimeException(e);
			}

		}
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/cart/{code}")
	@Operation(httpMethod = "GET", summary = "Get a chopping cart by code", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody ReadableShoppingCart getByCode(@PathVariable String code,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language, HttpServletResponse response) {

		try {
	
			ReadableShoppingCart cart = shoppingCartFacade.getByCode(code, merchantStore, language);

			if (cart == null) {
				response.sendError(404, "No ShoppingCart found for customer code : " + code);
				return null;
			}

			return cart;

		} catch (Exception e) {
			if(e instanceof ResourceNotFoundException exception) {
				throw exception;
			} else {
				throw new ServiceRuntimeException(e);
			}

		}
	}

	@Deprecated
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/customers/{id}/cart")
	@Operation(httpMethod = "POST", summary = "Add product to a specific customer shopping cart", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody ReadableShoppingCart addToCart(@PathVariable Long id,
			@Valid @RequestBody PersistableShoppingCartItem shoppingCartItem, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, HttpServletResponse response) {
		
		throw new OperationNotAllowedException("API is no more supported. Authenticate customer first then get customer cart");

	}

	@Deprecated
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/auth/customer/{id}/cart")
	@Operation(httpMethod = "GET", summary = "Get a shopping cart by customer id. Customer must be authenticated", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody ReadableShoppingCart getByCustomer(@PathVariable Long id, // customer
																					// id
			@RequestParam Optional<String> cart, // cart code
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language, HttpServletRequest request,
			HttpServletResponse response) {

		Principal principal = request.getUserPrincipal();

		// lookup customer
		Customer customer = customerService.getById(id);

		if (customer == null) {
			throw new ResourceNotFoundException("No Customer found for id [" + id + "]");
		}

		customerFacadev1.authorize(customer, principal);

		ReadableShoppingCart readableCart = shoppingCartFacadev1.get(cart, id, merchantStore, language);

		if (readableCart == null) {
			throw new ResourceNotFoundException("No cart found for customerid [" + id + "]");
		}

		return readableCart;

	}
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/auth/customer/cart")
	@Operation(httpMethod = "GET", summary = "Get a shopping cart by authenticated customer", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody ReadableShoppingCart getByCustomer(
			@RequestParam Optional<String> cart, // cart code
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language, 
			HttpServletRequest request,
			HttpServletResponse response) {

		Principal principal = request.getUserPrincipal();
		Customer customer = null;
		try {
			customer = customerFacade.getCustomerByUserName(principal.getName(), merchantStore);
		} catch (Exception e) {
			throw new ServiceRuntimeException("Exception while getting customer [ " + principal.getName() + "]");
		}
		
		if (customer == null) {
			throw new ResourceNotFoundException("No Customer found for principal[" + principal.getName() + "]");
		}
		
		customerFacadev1.authorize(customer, principal);
		ReadableShoppingCart readableCart = shoppingCartFacadev1.get(cart, customer.getId(), merchantStore, language);

		if (readableCart == null) {
			throw new ResourceNotFoundException("No cart found for customer [" + principal.getName() + "]");
		}

		return readableCart;

	}

	@DeleteMapping(value = "/cart/{code}/product/{sku}", produces = { APPLICATION_JSON_VALUE })
	@Operation(httpMethod = "DELETE", summary = "Remove a product from a specific cart", description = "If body set to true returns remaining cart in body, empty cart gives empty body. If body set to false no body ")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en"),
			@Parameter(name = "body", defaultValue = "false"), })
	public ResponseEntity<ReadableShoppingCart> deleteCartItem(@PathVariable("code") String cartCode,
			@PathVariable String sku, 
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			@RequestParam(defaultValue = "false") boolean body) throws Exception {

		ReadableShoppingCart updatedCart = shoppingCartFacade.removeShoppingCartItem(cartCode, sku, merchantStore,
				language, body);
		if (body) {
			return new ResponseEntity<>(updatedCart, HttpStatus.OK);
		}
		return new ResponseEntity<>(updatedCart, HttpStatus.NO_CONTENT);
	}
}
