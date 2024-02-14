package tacos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import tacos.Ingredient.Type;
import tacos.data.IngredientRepository;

//automatically generate an SLF4J (Simple Logging Facade for Java)
// Logger static property in the class.
@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder")//Holds state for the whole order
public class DesignTacoController {

	private final IngredientRepository ingredientRepo;
	
	@Autowired
	public DesignTacoController( IngredientRepository ingredientRepo) {
		this.ingredientRepo = ingredientRepo;
	}
	
	@ModelAttribute
	public void addIngredientsToModel(Model model) {
		Iterable<Ingredient> ingredients = ingredientRepo.findAll();
		Type[] types = Ingredient.Type.values();
		for(Type type:types) {
			model.addAttribute(type.toString().toLowerCase(), 
					filterByType((List<Ingredient>) ingredients, type));
		}
	}
	//https://github.com/habuma/spring-in-action-6-samples/blob/main/ch03/tacos-sd-jdbc/src/main/java/tacos/web/DesignTacoController.java
	/* HARCODED
	@ModelAttribute
	public void addIngredientsToModel(Model model) {
		List<Ingredient> ingredients = Arrays.asList(
				new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),
				new Ingredient("COTO", "Corn Tortilla", Type.WRAP),
				new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
				new Ingredient("CARN", "Carnitas", Type.PROTEIN),
				new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES),
				new Ingredient("LETC", "Lettuce", Type.VEGGIES),
				new Ingredient("CHED", "Cheddar", Type.CHEESE),
				new Ingredient("JACK", "Monterrey Jack", Type.CHEESE),
				new Ingredient("SLSA", "Salsa", Type.SAUCE),
				new Ingredient("SRCR", "Sour Cream", Type.SAUCE)
			);
		Type[] types = Ingredient.Type.values();
		for(Type type:types) {
			model.addAttribute(type.toString().toLowerCase(),
					filterByType(ingredients,type));
		}
	}
	*/
	@ModelAttribute(name = "tacoOrder")
	public TacoOrder order() {
		return new TacoOrder();
	}

	//Placed in the model so the view has a non null object to render
	@ModelAttribute(name = "taco")
	public Taco taco() {
		return new Taco();
	}
	
	//Manages the Get requests and shows the name of the view
	@GetMapping
	public String showDesignForm() {
		return "design";
	}
	
	@PostMapping
	public String processTaco(
			@Valid Taco taco, Errors errors,
			@ModelAttribute TacoOrder tacoOrder) {
		if(errors.hasErrors()) {
			return "design";
		}
		tacoOrder.addTaco(taco);
		log.info("Processing taco: {}", taco);
		
		return "redirect:/orders/current";
	}
	
	private Iterable<Ingredient> filterByType(
			//List<Ingredient> ingredients, Type type) {
			List<Ingredient> ingredients, Type type) {
		return ingredients
				.stream()
				.filter(x -> x.getType().equals(type))
				.collect(Collectors.toList());
	}
}
