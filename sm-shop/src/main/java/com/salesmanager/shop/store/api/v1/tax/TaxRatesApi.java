package com.salesmanager.shop.store.api.v1.tax;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
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
import com.salesmanager.shop.model.tax.PersistableTaxRate;
import com.salesmanager.shop.model.tax.ReadableTaxRate;
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
@Tag(name = "Tax Rates", description = "Tax Rates API")

public class TaxRatesApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaxRatesApi.class);

	@Autowired
	private TaxFacade taxFacade;

	/** Create new tax rate for a given MerchantStore */
	@PostMapping("/private/tax/rate")
@Operation(summary = "Creates a taxRate", description = "Requires administration access", tags = { "Tax Rates" })
@Parameter(name = "storeCode", description = "Store code", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
	public Entity create(@ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language,
			@Valid @RequestBody PersistableTaxRate taxRate) {

		return taxFacade.createTaxRate(taxRate, merchantStore, language);

	}

	@GetMapping(value = "/private/tax/rate/unique", produces = MediaType.APPLICATION_JSON_VALUE)
@Operation(summary = "Verify if taxRate is unique", description = "", tags = { "Tax Rates" })
@Parameter(name = "storeCode", description = "Store code", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
	public ResponseEntity<EntityExists> exists(@RequestParam String code, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		boolean exists = taxFacade.existsTaxRate(code, merchantStore, language);
		return new ResponseEntity<EntityExists>(new EntityExists(exists), HttpStatus.OK);

	}

	/** Update tax rate for a given MerchantStore */
	@PutMapping("/private/tax/rate/{id}")
@Operation(summary = "Updates a taxRate", description = "Requires administration access", tags = { "Tax Rates" })
@Parameter(name = "storeCode", description = "Store code", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
	public void update(@ApiIgnore MerchantStore merchantStore, @PathVariable Long id, @ApiIgnore Language language,
			@Valid @RequestBody PersistableTaxRate taxRate) {

		taxRate.setId(id);
		taxFacade.updateTaxRate(id, taxRate, merchantStore, language);

	}

	@GetMapping(value = "/private/tax/rates", produces = MediaType.APPLICATION_JSON_VALUE)
@Operation(summary = "List taxRates by store", description = "", tags = { "Tax Rates" })
@Parameter(name = "storeCode", description = "Store code", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
	public ReadableEntityList<ReadableTaxRate> list(@RequestParam(defaultValue = "10") int count,
			@RequestParam(defaultValue = "0") int page, @ApiIgnore MerchantStore merchantStore,
			@ApiIgnore Language language) {

		return taxFacade.taxRates(merchantStore, language);

	}
	
	@GetMapping("/private/tax/rate/{id}")
@Operation(summary = "Get a taxRate by code", description = "Requires administration access", tags = { "Tax Rates" })
@Parameter(name = "storeCode", description = "Store code", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
	public ReadableTaxRate get(@ApiIgnore MerchantStore merchantStore, @PathVariable Long id, @ApiIgnore Language language) {

		return taxFacade.taxRate(id, merchantStore, language);

	}

	@DeleteMapping(value = "/private/tax/rate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
@Operation(summary = "Delete tax rate", description = "", tags = { "Tax Rates" })
@Parameter(name = "storeCode", description = "Store code", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
	public void delete(@PathVariable Long id, @ApiIgnore MerchantStore merchantStore, @ApiIgnore Language language) {

		taxFacade.deleteTaxRate(id, merchantStore, language);

	}

}
