package com.salesmanager.shop.store.api.v1.product;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.inventory.PersistableInventory;
import com.salesmanager.shop.model.catalog.product.inventory.ReadableInventory;
import com.salesmanager.shop.model.entity.ReadableEntityList;
import com.salesmanager.shop.store.api.exception.RestApiException;
import com.salesmanager.shop.store.controller.product.facade.ProductInventoryFacade;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1")
@Tag(tags = { "Product inventory resource (Product Inventory Api)" })
@SwaggerDefinition(tags = {
		@Tag(name = "Product inventory resource", description = "Manage inventory for a given product") })
public class ProductInventoryApi {

	@Autowired
	private ProductInventoryFacade productInventoryFacade;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductInventoryApi.class);

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping({ "/private/product/{productId}/inventory" })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody ReadableInventory create(@PathVariable Long productId,
			@Valid @RequestBody PersistableInventory inventory, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		inventory.setProductId(productId);
		return productInventoryFacade.add(inventory, merchantStore, language);
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping({ "/private/product/{productId}/inventory/{id}" })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public void update(
			@PathVariable Long productId, 
			@PathVariable Long id,
			@Valid @RequestBody PersistableInventory inventory, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {
		inventory.setId(id);
		inventory.setProductId(inventory.getProductId());
		inventory.setVariant(inventory.getVariant());
		inventory.setProductId(productId);
		productInventoryFacade.update(inventory, merchantStore, language);

	}

	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping({ "/private/product/{productId}/inventory/{id}" })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public void delete(
			@PathVariable Long productId, 
			@PathVariable Long id, 
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language) {

		productInventoryFacade.delete(productId, id, merchantStore);

	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = { "/private/product/{sku}/inventory" })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody ReadableEntityList<ReadableInventory> getBySku(
			@PathVariable String sku,
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer count) {

		return productInventoryFacade.get(sku, merchantStore, language, page, count);

	}
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = { "/private/product/inventory" })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody ReadableEntityList<ReadableInventory> getByProductId(
			@RequestParam Long productId,
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer count) {
		
		if(productId == null) {
			throw new RestApiException("Requires request parameter product id [/product/inventoty?productId");
		}

		return productInventoryFacade.get(productId, merchantStore, language, page, count);

	}

}
