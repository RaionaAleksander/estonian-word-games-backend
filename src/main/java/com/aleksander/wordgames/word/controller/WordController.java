package com.aleksander.wordgames.word.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.aleksander.wordgames.word.dto.request.WordFilterRequest;
import com.aleksander.wordgames.word.dto.request.WordRandomListRequest;
import com.aleksander.wordgames.word.dto.request.WordPageRequest;
import com.aleksander.wordgames.word.dto.request.WordSortRequest;
import com.aleksander.wordgames.word.dto.response.WordDefinitionsResponse;
import com.aleksander.wordgames.word.dto.response.WordExistsResponse;
import com.aleksander.wordgames.word.dto.response.WordPageResponse;
import com.aleksander.wordgames.word.dto.response.WordPatternResponse;
import com.aleksander.wordgames.word.dto.response.WordResponse;
import com.aleksander.wordgames.word.enums.SortOrder;
import com.aleksander.wordgames.word.enums.SortType;
import com.aleksander.wordgames.word.service.WordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/words")
public class WordController {

	private final WordService wordService;

	@GetMapping
	public WordPageResponse getWords(

			// filters
			@RequestParam(required = false) Integer minLength,
			@RequestParam(required = false) Integer maxLength,
			@RequestParam(required = false) String startsWith,
			@RequestParam(required = false) String endsWith,
			@RequestParam(required = false) List<String> contains,
			@RequestParam(required = false) List<String> notContains,
			@RequestParam(required = false) List<String> includeCategories,
			@RequestParam(required = false) List<String> excludeCategories,
			@RequestParam(required = false) String pattern,
			@RequestParam(required = false) List<String> excludedWords,

			// sorting
			@RequestParam(required = false) SortType sort,
			@RequestParam(required = false) SortOrder order,

			// pagination
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "20") Integer size) {

		WordFilterRequest filter = new WordFilterRequest(
				minLength,
				maxLength,
				startsWith,
				endsWith,
				contains,
				notContains,
				includeCategories,
				excludeCategories,
				pattern,
				excludedWords);

		WordSortRequest sortRequest = new WordSortRequest(
				sort,
				order);

		WordPageRequest request = new WordPageRequest(
				filter,
				sortRequest,
				page,
				size);

		return wordService.getWordsPageResponse(request);
	}

	@GetMapping("/random")
	public WordResponse getRandomWords(
			@RequestParam(required = false) Integer minLength,
			@RequestParam(required = false) Integer maxLength,
			@RequestParam(required = false) String startsWith,
			@RequestParam(required = false) String endsWith,
			@RequestParam(required = false) List<String> contains,
			@RequestParam(required = false) List<String> notContains,
			@RequestParam(required = false) List<String> includeCategories,
			@RequestParam(required = false) List<String> excludeCategories,
			@RequestParam(required = false) String pattern,
			@RequestParam(required = false) List<String> excludedWords,
			@RequestParam(defaultValue = "20") Integer limit) {

		WordFilterRequest filterRequest = new WordFilterRequest(
				minLength,
				maxLength,
				startsWith,
				endsWith,
				contains,
				notContains,
				includeCategories,
				excludeCategories,
				pattern,
				excludedWords);

		WordRandomListRequest request = new WordRandomListRequest(
				filterRequest,
				limit);

		return wordService.getRandomWordsResponse(request);
	}

	@GetMapping("/exists")
	public WordExistsResponse exists(@RequestParam String word) {
		return wordService.checkExists(word);
	}

	@GetMapping("/definitions")
	public WordDefinitionsResponse getDefinitions(
			@RequestParam String word,
			@RequestParam(required = false) Integer limit,
			@RequestParam(defaultValue = "false") boolean random) {

		return wordService.getDefinitionsResponse(word, limit, random);
	}

	@GetMapping("/pattern")
	public WordPatternResponse getPattern(
			@RequestParam String word,
			@RequestParam(required = false) Integer visibleLetters) {

		return wordService.getPatternResponse(word, visibleLetters);
	}
}