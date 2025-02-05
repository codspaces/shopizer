package com.salesmanager.shop.store.api.v1.product;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.review.ProductReviewService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.review.ProductReview;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import com.salesmanager.shop.model.catalog.product.PersistableProductReview;
import com.salesmanager.shop.model.catalog.product.ReadableProductReview;
import com.salesmanager.shop.store.controller.product.facade.ProductCommonFacade;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1")
public class ProductReviewApi {

  @Inject private ProductCommonFacade productCommonFacade;

  @Inject private ProductService productService;

  @Inject private ProductReviewService productReviewService;

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductReviewApi.class);

  @PostMapping({
        "/private/products/{id}/reviews",
        "/auth/products/{id}/reviews",
        "/auth/products/{id}/reviews",
        "/auth/products/{id}/reviews"
      })
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  @Parameters({
      @Parameter(name = "store", defaultValue = "DEFAULT"),
      @Parameter(name = "lang", defaultValue = "en")
  })
  public PersistableProductReview create(
      @PathVariable final Long id,
      @Valid @RequestBody PersistableProductReview review,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletRequest request,
      HttpServletResponse response) {

    try {
      // rating already exist
      ProductReview prodReview =
          productReviewService.getByProductAndCustomer(
              review.getProductId(), review.getCustomerId());
      if (prodReview != null) {
        response.sendError(500, "A review already exist for this customer and product");
        return null;
      }

      // rating maximum 5
      if (review.getRating() > Constants.MAX_REVIEW_RATING_SCORE) {
        response.sendError(503, "Maximum rating score is " + Constants.MAX_REVIEW_RATING_SCORE);
        return null;
      }

      review.setProductId(id);

      productCommonFacade.saveOrUpdateReview(review, merchantStore, language);

      return review;

    } catch (Exception e) {
      LOGGER.error("Error while saving product review", e);
      try {
        response.sendError(503, "Error while saving product review" + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }

  @GetMapping("/product/{id}/reviews")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @Parameters({
      @Parameter(name = "store", defaultValue = "DEFAULT"),
      @Parameter(name = "lang", defaultValue = "en")
  })
  public List<ReadableProductReview> getAll(
      @PathVariable final Long id,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      // product exist
      Product product = productService.getById(id);

      if (product == null) {
        response.sendError(404, "Product id " + id + " does not exists");
        return null;
      }

      List<ReadableProductReview> reviews =
    		  productCommonFacade.getProductReviews(product, merchantStore, language);

      return reviews;

    } catch (Exception e) {
      LOGGER.error("Error while getting product reviews", e);
      try {
        response.sendError(503, "Error while getting product reviews" + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }

  @PutMapping({
        "/private/products/{id}/reviews/{reviewid}",
        "/auth/products/{id}/reviews/{reviewid}"
      })
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @Parameters({
      @Parameter(name = "store", defaultValue = "DEFAULT"),
      @Parameter(name = "lang", defaultValue = "en")
  })
  public PersistableProductReview update(
      @PathVariable final Long id,
      @PathVariable final Long reviewId,
      @Valid @RequestBody PersistableProductReview review,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletRequest request,
      HttpServletResponse response) {

    try {
      ProductReview prodReview = productReviewService.getById(reviewId);
      if (prodReview == null) {
        response.sendError(404, "Product review with id " + reviewId + " does not exist");
        return null;
      }

      if (prodReview.getCustomer().getId().longValue() != review.getCustomerId().longValue()) {
        response.sendError(404, "Product review with id " + reviewId + " does not exist");
        return null;
      }

      // rating maximum 5
      if (review.getRating() > Constants.MAX_REVIEW_RATING_SCORE) {
        response.sendError(503, "Maximum rating score is " + Constants.MAX_REVIEW_RATING_SCORE);
        return null;
      }

      review.setProductId(id);

      productCommonFacade.saveOrUpdateReview(review, merchantStore, language);

      return review;

    } catch (Exception e) {
      LOGGER.error("Error while saving product review", e);
      try {
        response.sendError(503, "Error while saving product review" + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }

  @DeleteMapping({
        "/private/products/{id}/reviews/{reviewid}",
        "/auth/products/{id}/reviews/{reviewid}"
      })
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  @Parameters({
      @Parameter(name = "store", defaultValue = "DEFAULT"),
      @Parameter(name = "lang", defaultValue = "en")
  })
  public void delete(
      @PathVariable final Long id,
      @PathVariable final Long reviewId,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      ProductReview prodReview = productReviewService.getById(reviewId);
      if (prodReview == null) {
        response.sendError(404, "Product review with id " + reviewId + " does not exist");
        return;
      }

      if (prodReview.getProduct().getId().longValue() != id.longValue()) {
        response.sendError(404, "Product review with id " + reviewId + " does not exist");
        return;
      }

      productCommonFacade.deleteReview(prodReview, merchantStore, language);

    } catch (Exception e) {
      LOGGER.error("Error while deleting product review", e);
      try {
        response.sendError(503, "Error while deleting product review" + e.getMessage());
      } catch (Exception ignore) {
      }

      return;
    }
  }
}
