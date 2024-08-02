package dyad.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dyad.assemblers.StockModelAssembler;
import dyad.dao.StockService;
import dyad.entities.Stock;
import dyad.exceptions.StockNotFoundException;

@RestController
@RequestMapping(value = "/api/stocks", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class StocksControllerApi {

	private static final String NOT_FOUND_MSG = "{ \"error\": \"%s\", \"id\": %d }";

	@Autowired
	private StockService stockService;

	@Autowired
	private StockModelAssembler stockAssembler;

	@ExceptionHandler(StockNotFoundException.class)
	public ResponseEntity<?> stockNotFoundHandler(StockNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(String.format(NOT_FOUND_MSG, ex.getMessage(), ex.getId()));
	}

	@GetMapping("/{id}")
	public EntityModel<Stock> getStock(@PathVariable("id") long id) {
		Stock stock = stockService.findById(id).orElseThrow(() -> new StockNotFoundException(id));
	

		return stockAssembler.toModel(stock);
	}

	@GetMapping
	public CollectionModel<EntityModel<Stock>> getAllStocks() {
		return stockAssembler.toCollectionModel(stockService.findAll())
				.add(linkTo(methodOn(StocksControllerApi.class).getAllStocks()).withSelfRel());
	}
	
	@GetMapping("/new")
	public ResponseEntity<?> newStock() {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createStock(@RequestBody @Valid Stock stock, BindingResult result) {

		if (result.hasErrors()) {
			return ResponseEntity.unprocessableEntity().build();
		}

		Stock newStock = stockService.save(stock);
		EntityModel<Stock> entity = stockAssembler.toModel(newStock);

		return ResponseEntity.created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri()).build();
	}
	@DeleteMapping(path="{id}")
	public ResponseEntity<Void> deleteBookById(@PathVariable long id) {
	    try {
	        stockService.deleteById(id);
	        return ResponseEntity.ok().build();
	    } catch (ResourceNotFoundException ex) {
	        return ResponseEntity.notFound().build();
	    }
	}
}
