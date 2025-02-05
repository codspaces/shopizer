package com.salesmanager.shop.store.api.v1.customer;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.shop.model.customer.optin.PersistableCustomerOptin;
import com.salesmanager.shop.store.controller.customer.facade.CustomerFacade;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;


/**
 * Optin a customer to newsletter
 * @author carlsamson
 *
 */
@RestController
@RequestMapping(value = "/api/v1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(tags = { "Optin Customer to newsletter" })
@SwaggerDefinition(tags = { @Tag(name = "Manage customer subscription to newsletter", description = "Manage customer subscription to newsletter") })
public class CustomerNewsletterApi {

	@Inject
	private CustomerFacade customerFacade;


  /** Create new optin */
  @PostMapping("/newsletter")
  @Operation(
      httpMethod = "POST",
      summary = "Creates a newsletter optin",
      description = "")
  @Parameters({
      @Parameter(name = "store", defaultValue = "DEFAULT"),
      @Parameter(name = "lang", defaultValue = "en")
  })
  public void create(
      @Valid @RequestBody PersistableCustomerOptin optin,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language) {
		customerFacade.optinCustomer(optin, merchantStore);
	}

  @PutMapping("/newsletter/{email}")
  @Operation(
      httpMethod = "PUT",
      summary = "Updates a customer",
      description = "Requires administration access")
  public void update(
      @PathVariable String email,
      @Valid @RequestBody PersistableCustomer customer,
      HttpServletRequest request,
      HttpServletResponse response) {
    throw new UnsupportedOperationException();
  }

  @DeleteMapping("/newsletter/{email}")
  @Operation(
      httpMethod = "DELETE",
      summary = "Deletes a customer",
      description = "Requires administration access")
  public ResponseEntity<Void> delete(
      @PathVariable String email, HttpServletRequest request, HttpServletResponse response) {
    throw new UnsupportedOperationException();
  }
}
