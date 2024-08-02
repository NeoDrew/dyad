package dyad.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import dyad.controllers.StocksControllerApi;
import dyad.entities.Stock;

@Component
public class StockModelAssembler implements RepresentationModelAssembler<Stock, EntityModel<Stock>> {

	@Override
	public EntityModel<Stock> toModel(Stock stock) {
		return EntityModel.of(stock);//, linkTo(methodOn(stocksControllerApi.class).getstock(stock.getId())).withSelfRel(),
				// linkTo(methodOn(stocksControllerApi.class).getAllstocks()).withRel("stocks"));
	}
}
