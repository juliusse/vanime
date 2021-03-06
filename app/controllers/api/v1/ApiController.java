package controllers.api.v1;

import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;
import services.converter.ConverterService;
import services.converter.ConverterServiceImpl;
import services.converter.ConverterSettings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiController extends Controller {

    private ConverterService converterService;

    public ApiController() {
        converterService = new ConverterServiceImpl();
    }

    public Result convert(String number, int minWordLength, int maxConnectedNumbers) {
        try {
            final List<String> suggestions = converterService.convert(number, new ConverterSettings(minWordLength, maxConnectedNumbers));

            final JsonNode resultBody = new ObjectMapper().valueToTree(suggestions);

            response().setContentType("application/json");
            return ok(resultBody);
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }
}
