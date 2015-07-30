package org.generationcp.ibpworkbench.controller;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.generationcp.ibpworkbench.model.AskSupportFormModel;
import org.generationcp.ibpworkbench.security.WorkbenchEmailSenderService;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by cyrus on 7/21/15.
 */
@Controller
@RequestMapping(AskSupportController.URL)
public class AskSupportController {
	public static final String URL = "/support";

	public static final String SUCCESS = "success";
	public static final String ERRORS = "errors";

	@Resource
	private WorkbenchEmailSenderService workbenchEmailSenderService;

	@ModelAttribute("requestCategories")
	public List<String> getRequestCategories() {

		return Arrays.asList(AskSupportFormModel.CATEGORIES);
	}

	@RequestMapping(value = "/")
	public String index() {
		return "ask-support-form";
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public ResponseEntity<String> submit(@ModelAttribute("askSupportForm") AskSupportFormModel askSupportForm,HttpServletResponse response)
			throws JsonProcessingException {
		response.setHeader("Content-Type","text/html");

		Map<String,String> result = new HashMap<>();
			try {
				workbenchEmailSenderService.sendFeedback(askSupportForm);
				result.put("success","true");

			} catch (MiddlewareQueryException | MessagingException e) {
				result.put("success","false");
			}

		return new ResponseEntity<>((new ObjectMapper()).writeValueAsString(result),HttpStatus.OK);
	}

}