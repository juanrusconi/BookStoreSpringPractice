package controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

	@RequestMapping(method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ArrayList<Link>> getIndexes(){
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(ControllerLinkBuilder.linkTo(BookController.class).toString(), "books"));
		
		//TODO: add Link referring "People" collection
		
		
		return new ResponseEntity<ArrayList<Link>>((ArrayList<Link>)links, HttpStatus.OK);
	}
}
