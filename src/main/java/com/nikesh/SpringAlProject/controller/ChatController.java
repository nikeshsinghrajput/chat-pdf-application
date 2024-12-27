package com.nikesh.SpringAlProject.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.nikesh.SpringAlProject.payload.CricketResponse;
import com.nikesh.SpringAlProject.service.ChatService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

	@Autowired
	private ChatService chatService;
	
	@GetMapping("normal-chat")
	public ResponseEntity<String> generateResponse(@RequestParam String inputText)
	{
		String responseText=chatService.genrateResponse(inputText);
		return ResponseEntity.ok(responseText);
	}
	@GetMapping("flux-chat")
	public Flux<String> generateFluxResponse(@RequestParam String inputText)
	{
		Flux<String> responseText=chatService.streamResponse(inputText);
		return  responseText;
	}
	@GetMapping("cricket-chatboat")
	public ResponseEntity<CricketResponse> generateCricketChatResponse(@RequestParam String inputText) throws JsonMappingException, JsonProcessingException
	{
		CricketResponse responseText=chatService.generateCricketResponse(inputText);
		return  ResponseEntity.ok(responseText);
	}
	@GetMapping("/images")
	public ResponseEntity<List<String>> generateImages(
			@RequestParam("imageDescription") String imageDesc,
			@RequestParam(value="size",required = false,defaultValue = "512x512")String size,
			@RequestParam(value="numberOfImages",required=false,defaultValue = "1") int numbers) throws IOException
	{
		return ResponseEntity.ok(chatService.generateImages(imageDesc, size, numbers));
		 
	}
}
