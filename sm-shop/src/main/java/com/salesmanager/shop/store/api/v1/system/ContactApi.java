package com.salesmanager.shop.store.api.v1.system;

import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.shop.ContactForm;
import com.salesmanager.shop.utils.EmailTemplatesUtils;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1")
@Tag(tags = {"Contact form api"})
@SwaggerDefinition(tags = {
    @Tag(name = "Contact store resource", description = "Contact form")
})
public class ContactApi {


  @Inject private LanguageService languageService;

  @Inject private EmailTemplatesUtils emailTemplatesUtils;

  @PostMapping("/contact")
  @Operation(
      httpMethod = "POST",
      summary = "Sends an email contact us to store owner",
      description = "")
  @Parameters({
      @Parameter(name = "store", defaultValue = "DEFAULT"),
      @Parameter(name = "lang", defaultValue = "en")
  })
  public ResponseEntity<Void> contact(
      @Valid @RequestBody ContactForm contact,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletRequest request) {
    Locale locale = languageService.toLocale(language, merchantStore);
    emailTemplatesUtils.sendContactEmail(contact, merchantStore, locale, request.getContextPath());
    return new ResponseEntity<Void>(HttpStatus.OK);
  }
}
