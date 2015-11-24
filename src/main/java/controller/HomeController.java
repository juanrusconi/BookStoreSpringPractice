package controller;

import java.util.ArrayList;
import java.util.List;

/* ---- Spring HATEOAS ---- */
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
/* ---- Spring HTTP ---- */
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
/* ---- Spring annotations ---- */
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

	@RequestMapping(method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ArrayList<Link>> getIndexes(){
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(ControllerLinkBuilder.linkTo(HomeController.class).toString(), "home"));
		links.add(new Link(ControllerLinkBuilder.linkTo(BookController.class).toString(), "books"));
		links.add(new Link(ControllerLinkBuilder.linkTo(PersonController.class).toString(), "persons"));
		
		return new ResponseEntity<ArrayList<Link>>((ArrayList<Link>)links, HttpStatus.OK);
	}
}
