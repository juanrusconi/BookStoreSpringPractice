package controller;

import java.util.ArrayList;
import java.util.List;

/* ---- Spring HATEOAS ---- */
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
/* ---- Spring annotations ---- */
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookstore")
public class HomeController {

	@RequestMapping(method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody List<Link> getIndexes(){
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(ControllerLinkBuilder.linkTo(HomeController.class).toString(), Link.REL_SELF));
		links.add(new Link(ControllerLinkBuilder.linkTo(BookController.class).toString(), "books"));
		links.add(new Link(ControllerLinkBuilder.linkTo(PersonController.class).toString(), "persons"));
		
		return links;
	}
}
