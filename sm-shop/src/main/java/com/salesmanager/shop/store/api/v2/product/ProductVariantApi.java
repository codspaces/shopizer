package com.salesmanager.shop.store.api.v2.product;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.catalog.product.product.variant.PersistableProductVariant;
import com.salesmanager.shop.model.catalog.product.product.variant.ReadableProductVariant;
import com.salesmanager.shop.model.entity.Entity;
import com.salesmanager.shop.model.entity.EntityExists;
import com.salesmanager.shop.model.entity.ReadableEntityList;
import com.salesmanager.shop.store.api.exception.UnauthorizedException;
import com.salesmanager.shop.store.controller.product.facade.ProductVariantFacade;
import com.salesmanager.shop.store.controller.user.facade.UserFacade;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

/**
 * Api to manage productVariant
 * 
 * Product variant allows to specify product
 * size, sku and options related to this product variant
 * 
 * @author carlsamson
 *
 */
@Controller
@RequestMapping("/api/v2")
@Tag(tags = { "Product variants api" })
@SwaggerDefinition(tags = {
		@Tag(name = "Product variants resource", description = "Manage inventory for a given product") })
public class ProductVariantApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductVariantApi.class);

	@Autowired
	private ProductVariantFacade productVariantFacade;

	@Inject
	private UserFacade userFacade;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = { "/private/product/{productId}/variant" })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
		@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody Entity create(
			@Valid @RequestBody PersistableProductVariant variant, 
			@PathVariable Long productId,
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language) {

		String authenticatedUser = userFacade.authenticatedUser();
		if (authenticatedUser == null) {
			throw new UnauthorizedException();
		}

		userFacade.authorizedGroup(authenticatedUser, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_ADMIN_CATALOGUE, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()));

		Long id = productVariantFacade.create(variant, productId, merchantStore, language);
		return new Entity(id);
		
	}


	@ResponseStatus(HttpStatus.OK)
	@PutMapping(value = { "/private/product/{id}/variant/{variantId}" })
	@Operation(httpMethod = "PUT", summary = "Update product variant", description = "")
	public @ResponseBody void update(@PathVariable Long id, @PathVariable Long variantId,
			@Valid @RequestBody PersistableProductVariant variant, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		String authenticatedUser = userFacade.authenticatedUser();
		if (authenticatedUser == null) {
			throw new UnauthorizedException();
		}

		userFacade.authorizedGroup(authenticatedUser, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_ADMIN_CATALOGUE, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()));

		productVariantFacade.update(variantId, variant, id, merchantStore, language);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = { "/private/product/{id}/variant/{sku}/unique" }, produces = "application/json")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	@Operation(httpMethod = "GET", summary = "Check if option set code already exists", description = "")
	public @ResponseBody ResponseEntity<EntityExists> exists(
			@PathVariable Long id, 
			@PathVariable String sku,
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language) {

		String authenticatedUser = userFacade.authenticatedUser();
		if (authenticatedUser == null) {
			throw new UnauthorizedException();
		}

		userFacade.authorizedGroup(authenticatedUser, Stream.of(Constants.GROUP_SUPERADMIN, Constants.GROUP_ADMIN,
				Constants.GROUP_ADMIN_CATALOGUE, Constants.GROUP_ADMIN_RETAIL).collect(Collectors.toList()));

		boolean exist = productVariantFacade.exists(sku, merchantStore, id, language);
		return new ResponseEntity<EntityExists>(new EntityExists(exist), HttpStatus.OK);

	}

	@GetMapping(value = "/private/product/{id}/variant/{variantId}", produces = "application/json")
	@Operation(httpMethod = "GET", summary = "Get a productVariant by id", description = "For administration and shop purpose. Specifying ?merchant is required otherwise it falls back to DEFAULT")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Single product found", response = ReadableProductVariant.class) })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody ReadableProductVariant get(
			@PathVariable final Long id, 
			@PathVariable Long variantId,
			@RequestParam(required = false) String lang, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) throws Exception {

		return productVariantFacade.get(variantId, id, merchantStore, language);

	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping({ "/private/product/{id}/variants" })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody ReadableEntityList<ReadableProductVariant> list(@PathVariable final Long id,
			@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer count) {

		return productVariantFacade.list(id, merchantStore, language, page, count);

	}
	
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping({ "/private/product/{id}/variant/{variantId}" })
	@Parameters({ 
		@Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public void delete(
			@PathVariable Long id,
			@PathVariable Long variantId,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		productVariantFacade.delete(variantId, id, merchantStore);


	}
	
	
	/**
	
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(value = { "/private/product/{id}/{variantId}/image" }, method = RequestMethod.POST)
	@ApiImplicitParams({ @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
			@ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en") })
	public void addvariantImage(
			@PathVariable Long id,
			@RequestParam(name = "file", required = true) MultipartFile file, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language, 
			HttpServletRequest request, 
			HttpServletResponse response) {

		//productOptionFacade.addOptionValueImage(file, id, merchantStore, language);


	}
	
	**/
	
	

}
