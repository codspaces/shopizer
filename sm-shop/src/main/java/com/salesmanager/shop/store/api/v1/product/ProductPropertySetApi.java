package com.salesmanager.shop.store.api.v1.product;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.product.attribute.optionset.PersistableProductOptionSet;
import com.salesmanager.shop.model.catalog.product.attribute.optionset.ReadableProductOptionSet;
import com.salesmanager.shop.model.entity.EntityExists;
import com.salesmanager.shop.store.controller.product.facade.ProductOptionSetFacade;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1")
@Tag(tags = { "Product property set regroupment management resource (Product Options Set Management Api)" })
@SwaggerDefinition(tags = {
		@Tag(name = "Product property set regroupment management resource resource", description = "Edit product property set") })
public class ProductPropertySetApi {

	@Autowired
	private ProductOptionSetFacade productOptionSetFacade;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping({ "/private/product/property/set" })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public void create(
			@Valid @RequestBody PersistableProductOptionSet optionSet, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		productOptionSetFacade.create(optionSet, merchantStore, language);

	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = { "/private/product/property/set/unique" }, produces = MediaType.APPLICATION_JSON_VALUE)
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	@Operation(httpMethod = "GET", summary = "Check if option set code already exists", description = "")
	public ResponseEntity<EntityExists> exists(
			@RequestParam String code,
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language) {

		boolean isOptionExist = productOptionSetFacade.exists(code, merchantStore);
		return new ResponseEntity<EntityExists>(new EntityExists(isOptionExist), HttpStatus.OK);
	}


	@ResponseStatus(HttpStatus.OK)
	@GetMapping({ "/private/product/property/set/{id}" })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	@ResponseBody
	public ReadableProductOptionSet get(
			@PathVariable Long id, 
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		return productOptionSetFacade.get(id, merchantStore, language);

	}


	@ResponseStatus(HttpStatus.OK)
	@PutMapping({ "/private/product/property/set/{id}" })
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public void update(
			@Valid @RequestBody PersistableProductOptionSet option, 
			@PathVariable Long id,
			@ApiIgnore MerchantStore merchantStore, 
			@ApiIgnore Language language) {
		
		option.setId(id);
		productOptionSetFacade.update(id, option, merchantStore, language);

	}


	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping({ "/private/product/property/set/{id}" })
	@Parameters({ 
		@Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public void delete(
			@PathVariable Long id,
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		productOptionSetFacade.delete(id, merchantStore);

	}

	/**
	 * Get property set by store
	 * filter by product type
	 * @param merchantStore
	 * @param language
	 * @return
	 */
	@ResponseStatus(HttpStatus.OK)
	@GetMapping({ "/private/product/property/set" })
	@Parameters({ 
		@Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public @ResponseBody List<ReadableProductOptionSet> list(
			@ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language,
			@RequestParam(value = "productType", required = false) String type) {

		if(!StringUtils.isBlank(type)) {
			return productOptionSetFacade.list(merchantStore, language, type);
		} else {
			return productOptionSetFacade.list(merchantStore, language);
		}
		
		
	}
	

}