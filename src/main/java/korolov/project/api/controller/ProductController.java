package korolov.project.api.controller;

import korolov.project.api.converter.ProductConverter;
import korolov.project.api.dto.ProductDTO;
import korolov.project.api.exceptions.EntityStateException;
import korolov.project.api.exceptions.HasRelationException;
import korolov.project.business.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //CREATE createProduct or addProduct POST
    @PostMapping("/products")
    ProductDTO create(@RequestBody ProductDTO productDTO) {
        try {
            productDTO = ProductConverter.fromModel(productService.create(ProductConverter.toModel(productDTO)));
        } catch (EntityStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product already exists");
        }
        return productDTO;
    }

    //READ showAllProducts GET
    @GetMapping("/products")
    Collection<ProductDTO> getAll() {
        return ProductConverter.fromModels(productService.readAll());
    }

    //READ showProduct GET
    @GetMapping("/products/{id}")
    ProductDTO getOne(@PathVariable long id) {
        return ProductConverter.fromModel(productService.readById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        ));
    }

    //UPDATE editProduct PUT
    @PutMapping("/products/{id}")
    ProductDTO update(@PathVariable long id, @RequestBody ProductDTO productDTO) {
        if (productDTO.getProductId() != id)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product ids do not match");
        try {
            productDTO = ProductConverter.fromModel(productService.update(ProductConverter.toModel(productDTO)));
        } catch (EntityStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found");
        }
        return productDTO;
    }

    //DELETE deleteProduct hideProduct/*if we dont want to delete but want to hide from all clients*/ DELETE
    @DeleteMapping("/products/{id}")
    void delete(@PathVariable long id) {
        if (productService.readById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product to delete not found");
        }
        try {
            productService.deleteById(id);
        } catch (HasRelationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not to delete product");
        }
    }

}
