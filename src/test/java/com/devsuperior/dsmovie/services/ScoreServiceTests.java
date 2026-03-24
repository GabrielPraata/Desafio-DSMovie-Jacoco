package com.devsuperior.dsmovie.services;

import static org.mockito.ArgumentMatchers.any;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private ScoreRepository scoreRepository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private UserService userService;


	private ScoreEntity score;
	private long nonExistingMovieId;
	private MovieEntity movie;
	private UserEntity user;
	private ScoreDTO scoreDTO;

	@BeforeEach
	void setUp() throws Exception {
		score = ScoreFactory.createScoreEntity();
		movie = score.getId().getMovie();
		user  = UserFactory.createUserEntity();
		scoreDTO = new ScoreDTO(movie.getId(), score.getValue());
		nonExistingMovieId = 2L;

		Mockito.when(userService.authenticated()).thenReturn(user);
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
		Mockito.when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
		Mockito.when(scoreRepository.saveAndFlush(any(ScoreEntity.class))).thenReturn(score);
		Mockito.when(movieRepository.save(any(MovieEntity.class))).thenReturn(movie);

	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {

		MovieDTO result = service.saveScore(scoreDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(movie.getTitle(), result.getTitle());
		Assertions.assertEquals(ScoreFactory.scoreValue, result.getScore());
		Assertions.assertEquals(1, result.getCount());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		ScoreDTO scoreDTO = new ScoreDTO(nonExistingMovieId, score.getValue());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDTO);
		});
	}
}
