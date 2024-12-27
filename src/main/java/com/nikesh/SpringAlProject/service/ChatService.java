package com.nikesh.SpringAlProject.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikesh.SpringAlProject.payload.CricketResponse;

import reactor.core.publisher.Flux;

@Service
public class ChatService {

	@Autowired
	private ChatModel chatModel;

	@Autowired
	private StreamingChatModel streamingChatModel;
	
	@Autowired
	private OpenAiImageModel openAiImageModel;

	public String genrateResponse(String inputText) {
		String response = chatModel.call(inputText);

		return response;
	}

	public Flux<String> streamResponse(String inputText) {
		Flux<String> response = chatModel.stream(inputText);

		return response;
	}

	public CricketResponse generateCricketResponse(String inputText) throws JsonMappingException, JsonProcessingException
	{
		String prompt="As a cricket expert. Answer this question : "+inputText+
				".If the above question is not related to cricket. "
				+ "return sorry dear this question is out of context "+
				"Give a plainJSON response that must contain 'content' as a key and your response and a value"+
				"Only return valid JSON without code blocks or addational formatting.";
	
		ChatResponse cricketResponse=chatModel.call(new Prompt(prompt));
		String stringCricketResponse=cricketResponse.getResult().getOutput().getContent();
		System.out.println("chat response :"+stringCricketResponse);
		ObjectMapper mapper=new ObjectMapper();
		CricketResponse cricketChatResponse=mapper.readValue(stringCricketResponse, CricketResponse.class);
		return cricketChatResponse;
	}
	public List<String> generateImages(String imageDesc,String size,int numbers) throws IOException
	{
		String template = this.loadPromptTempelate("prompts/image_bot.txt");
		String prompt= this.putValueInPromptTemplate(template, Map.of(
				"numberOfImages",numbers +"",
				"description",imageDesc,
				"size",size));
		ImageResponse imageResponse = openAiImageModel.call(new ImagePrompt(prompt, OpenAiImageOptions.builder()
				.withModel("dall-e-3").withHeight(1024)
				.withWidth(1024).withN(1)
				.withQuality("standard")
				.build()));
		List<String> imageUrls = imageResponse.getResults().stream().map(generation->generation.getOutput().getUrl()).collect(Collectors.toList());
	     return imageUrls;
	}
	 
	
	//load prompt from classpath
	public String loadPromptTempelate(String fileName) throws IOException
	{
		Path filePath =new ClassPathResource(fileName).getFile().toPath();
		
		return Files.readString(filePath);
	}
	public String putValueInPromptTemplate(String templete ,Map<String,String> variables)
	{
		for(Map.Entry<String, String> entry : variables.entrySet())
		{
			templete=templete.replace("{"+entry.getKey()+"}", entry.getValue());
		}
		return templete;
	}
	//put cvalue to prompt
//	public List<String> generateImages(String imageDesc, String size, int numberOfImages) throws IOException {
//	    // Load the prompt template
//	    String template = loadPromptTemplate("prompts/image_bot.txt");
//
//	    // Replace placeholders in the template with actual values
//	    String prompt = populateTemplateWithValues(template, Map.of(
//	            "numberOfImages", String.valueOf(numberOfImages),
//	            "description", imageDesc,
//	            "size", size
//	    ));
//
//	    // Create options for generating multiple images
//	    OpenAiImageOptions imageOptions = OpenAiImageOptions.builder()
//	            .withModel("dall-e-3")
//	            .withHeight(1024)
//	            .withWidth(1024)
//	            .withN(numberOfImages) // Generate multiple images
//	            .withQuality("standard")
//	            .build();
//
//	    // Call the OpenAI model with the prompt and options
//	    ImageResponse imageResponse = openAiImageModel.call(new ImagePrompt(prompt, imageOptions));
//
//	    // Extract and return URLs of generated images
//	    return imageResponse.getResults().stream()
//	            .map(generation -> generation.getOutput().getUrl())
//	            .collect(Collectors.toList());
//	}
//
//	// Load the prompt template from the classpath
//	public String loadPromptTemplate(String fileName) throws IOException {
//	    Path filePath = new ClassPathResource(fileName).getFile().toPath();
//	    return Files.readString(filePath);
//	}
//
//	// Populate a template string with values from a map
//	public String populateTemplateWithValues(String template, Map<String, String> variables) {
//	    for (Map.Entry<String, String> entry : variables.entrySet()) {
//	        template = template.replace("{" + entry.getKey() + "}", entry.getValue());
//	    }
//	    return template;
//	}


	
}
