package com.salesmanager.shop.store.api.v1.search;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.catalog.SearchProductRequest;
import com.salesmanager.shop.model.entity.ValueList;
import com.salesmanager.shop.store.controller.search.facade.SearchFacade;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import modules.commons.search.request.SearchItem;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Api for searching shopizer catalog based on search term when filtering products based on product
 * attribute is required, see /api/v1/product
 *
 * @author c.samson
 */
@RestController
@RequestMapping("/api/v1")
@Tag(tags = {"Search products and search word/sentence completion functionality (Search Api)"})
@SwaggerDefinition(tags = {
    @Tag(name = "Search products resource", description = "Search products and search term completion functionality")
})
public class SearchApi {

  @Inject private SearchFacade searchFacade;


  /**
   * Search products from underlying elastic search
   */
  @PostMapping("/search")
  @Parameters({
    @Parameter(name = "store", defaultValue = "DEFAULT"),
    @Parameter(name = "lang", defaultValue = "en")
  })
  
  //TODO use total, count and page
  public @ResponseBody List<SearchItem> search(
      @RequestBody SearchProductRequest searchRequest,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language) {

    return searchFacade.search(merchantStore, language, searchRequest);
  }

  @PostMapping("/search/autocomplete")
  @Parameters({
    @Parameter(name = "store", defaultValue = "DEFAULT"),
    @Parameter(name = "lang", defaultValue = "en")
  })
  public @ResponseBody ValueList autocomplete(
      @RequestBody SearchProductRequest searchRequest,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language) {
    return searchFacade.autocompleteRequest(searchRequest.getQuery(), merchantStore, language);
  }
}
