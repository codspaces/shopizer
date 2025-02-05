package com.salesmanager.shop.store.api.v1.configurations;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.configuration.ReadableConfiguration;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/api/v1")
@Tag(tags = { "Configurations management" })
@SwaggerDefinition(tags = {
		@Tag(name = "Configurations management", description = "Configurations management for modules") })
public class ConfigurationsApi {
	
	
	  /** Configurations of modules */
	  @PostMapping("/private/configurations/payment")
	  @Operation(
	      httpMethod = "POST",
	      summary = "Manages payment configurations",
	      description = "Requires administration access")
	  @Parameters({
	      @Parameter(name = "store", defaultValue = "DEFAULT")
	  })
	  public Void create(
	      @ApiIgnore MerchantStore merchantStore,
	      @ApiIgnore Language language) {
	      //return customerFacade.create(customer, merchantStore, language);
		  return null;

	  }
	  
	  
	  /** Configurations of payment modules */
	  @GetMapping("/private/configurations/payment")
	  @Operation(
	      httpMethod = "GET",
	      summary = "List payment configurations summary",
	      description = "Requires administration access")
	  @Parameters({
	      @Parameter(name = "store", defaultValue = "DEFAULT")
	  })
	  public List<ReadableConfiguration> listPaymentConfigurations(
	      @ApiIgnore MerchantStore merchantStore,
	      @ApiIgnore Language language) {
	      //return customerFacade.create(customer, merchantStore, language);
		  return null;

	  }
	  
	  
	  
	  
	  /** Configurations of shipping modules */
	  @GetMapping("/private/configurations/shipping")
	  @Operation(
	      httpMethod = "GET",
	      summary = "List shipping configurations summary",
	      description = "Requires administration access")
	  @Parameters({
	      @Parameter(name = "store", defaultValue = "DEFAULT")
	  })
	  public List<ReadableConfiguration> listShippingConfigurations(
	      @ApiIgnore MerchantStore merchantStore,
	      @ApiIgnore Language language) {
	      //return customerFacade.create(customer, merchantStore, language);
		  return null;

	  }
	
	
	
	
	

}
