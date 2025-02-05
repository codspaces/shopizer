package com.salesmanager.shop.store.api.v1.product;

import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.type.PersistableProductType;
import com.salesmanager.shop.model.catalog.product.type.ReadableProductType;
import com.salesmanager.shop.model.catalog.product.type.ReadableProductTypeList;
import com.salesmanager.shop.model.entity.Entity;
import com.salesmanager.shop.model.entity.EntityExists;
import com.salesmanager.shop.store.controller.product.facade.ProductTypeFacade;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

/**
 * API to create, read, update and delete a Product API to create Manufacturer
 *
 * @author Carl Samson
 */
@RestController
@RequestMapping("/api/v1")
@Tag(tags = { "Product type resource (Product Type Api)" })
@SwaggerDefinition(tags = { @Tag(name = "Product type resource", description = "Manage product types") })
public class ProductTypeApi {

	@Inject
	private ProductTypeFacade productTypeFacade;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductTypeApi.class);

	@GetMapping(value = "/private/product/types", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(httpMethod = "GET", summary = "Get product types list", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ReadableProductTypeList list(@RequestParam(defaultValue = "10") int count,
			@RequestParam(defaultValue = "0") int page, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		return productTypeFacade.getByMerchant(merchantStore, language, count, page);

	}

	@GetMapping(value = "/private/product/type/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(httpMethod = "GET", summary = "Get product type", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ReadableProductType get(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		return productTypeFacade.get(merchantStore, id, language);

	}

	@GetMapping(value = "/private/product/type/unique", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(httpMethod = "GET", summary = "Verify if product type is unique", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ResponseEntity<EntityExists> exists(@RequestParam String code, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		boolean exists = productTypeFacade.exists(code, merchantStore, language);
		return new ResponseEntity<EntityExists>(new EntityExists(exists), HttpStatus.OK);

	}

	@PostMapping(value = "/private/product/type", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(httpMethod = "POST", summary = "Create product type", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public Entity create(@RequestBody PersistableProductType type, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		Long id = productTypeFacade.save(type, merchantStore, language);
		Entity entity = new Entity();
		entity.setId(id);
		return entity;

	}

	@PutMapping(value = "/private/product/type/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(httpMethod = "PUT", summary = "Update product type", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public void update(@RequestBody PersistableProductType type, @PathVariable Long id,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		productTypeFacade.update(type, id, merchantStore, language);

	}

	@DeleteMapping(value = "/private/product/type/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(httpMethod = "DELETE", summary = "Delete product type", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public void delete(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		productTypeFacade.delete(id, merchantStore, language);

	}

}
