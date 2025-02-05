package com.salesmanager.shop.store.api.v1.tax;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.salesmanager.shop.model.entity.Entity;
import com.salesmanager.shop.model.entity.EntityExists;
import com.salesmanager.shop.model.entity.ReadableEntityList;
import com.salesmanager.shop.model.tax.PersistableTaxClass;
import com.salesmanager.shop.model.tax.ReadableTaxClass;
import com.salesmanager.shop.store.controller.tax.facade.TaxFacade;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

/**
 * Tax class management
 * 
 * @author carlsamson
 *
 */

@RestController
@RequestMapping(value = "/api/v1")
@Tag(tags = { "Tax class management resource (Tax class management Api)" })
@SwaggerDefinition(tags = { @Tag(name = "Tax class management resource", description = "Manage tax classes") })
public class TaxClassApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaxClassApi.class);

	@Autowired
	private TaxFacade taxFacade;

	/** Create new tax class for a given MerchantStore */
	@PostMapping("/private/tax/class")
	@Operation(httpMethod = "POST", summary = "Creates a taxClass", description = "Requires administration access")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public Entity create(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			@Valid @RequestBody PersistableTaxClass taxClass) {

		return taxFacade.createTaxClass(taxClass, merchantStore, language);

	}

	@GetMapping(value = "/private/tax/class/unique", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(httpMethod = "GET", summary = "Verify if taxClass is unique", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ResponseEntity<EntityExists> exists(@RequestParam String code, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		boolean exists = taxFacade.existsTaxClass(code, merchantStore, language);
		return new ResponseEntity<EntityExists>(new EntityExists(exists), HttpStatus.OK);

	}

	/** Update tax class for a given MerchantStore */
	@PutMapping("/private/tax/class/{id}")
	@Operation(httpMethod = "PUT", summary = "Updates a taxClass", description = "Requires administration access")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public void update(@ApiIgnore MerchantStore merchantStore, @PathVariable Long id, @ApiIgnore Language language,
			@Valid @RequestBody PersistableTaxClass taxClass) {

		taxClass.setId(id);
		taxFacade.updateTaxClass(id, taxClass, merchantStore, language);

	}

	@GetMapping(value = "/private/tax/class", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(httpMethod = "GET", summary = "List taxClasses by store", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public ReadableEntityList<ReadableTaxClass> list(@RequestParam(defaultValue = "10") int count,
			@RequestParam(defaultValue = "0") int page, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		return taxFacade.taxClasses(merchantStore, language);

	}
	
	@GetMapping("/private/tax/class/{code}")
	@Operation(httpMethod = "GET", summary = "Get a taxClass by code", description = "Requires administration access")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT") })
	public ReadableTaxClass get(@ApiIgnore MerchantStore merchantStore, @PathVariable String code, @ApiIgnore Language language) {

		return taxFacade.taxClass(code, merchantStore, language);

	}

	@DeleteMapping(value = "/private/tax/class/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(httpMethod = "DELETE", summary = "Delete tax class", description = "")
	@Parameters({ @Parameter(name = "store", defaultValue = "DEFAULT"),
			@Parameter(name = "lang", defaultValue = "en") })
	public void delete(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		taxFacade.deleteTaxClass(id, merchantStore, language);

	}

}
