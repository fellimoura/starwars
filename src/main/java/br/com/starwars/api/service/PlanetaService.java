package br.com.starwars.api.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import br.com.starwars.api.dto.PlanetaDTO;
import br.com.starwars.api.dto.PlanetsDTO;
import br.com.starwars.api.entity.PlanetaEntity;
import br.com.starwars.api.repository.PlanetaRepository;

@Service
public class PlanetaService {

	@Autowired
	private PlanetaRepository repository;
	
	private RestTemplate restTemplate;
	
	private String PATH_PLANETS = "https://swapi.co/api/planets/";
	
	@Transactional
	public PlanetaEntity adicionar(PlanetaDTO planeta) throws URISyntaxException {
		
		if (!StringUtils.isEmpty(planeta)) {
			
			PlanetaEntity entity = new PlanetaEntity();
			
			entity.setClimate(planeta.getClima());
			entity.setName(planeta.getNome());
			entity.setTerrain(planeta.getTerreno());

			PlanetsDTO dto = consunoSWApi(planeta.getIdSWAPI());
			
			Long quantidade = dto.getFilms().stream().count();
			
			entity.setQuantity(quantidade.toString());
			
			repository.save(entity);
			
			return entity;
		}
		
		return null;
	}
	
	@Transactional
	public void remover(String id) {
		
		repository.deleteById(id);
	}
	
	public List<PlanetaEntity> findListPlaneta(String name) {
		
		if (!StringUtils.isEmpty(name)) {
			
			List<PlanetaEntity> planetaList = repository.findByName(name);
			
			return planetaList;
		}
		
		return null;
	}
	
	public PlanetaEntity findByPlaneta(String id) {
		
		if (id != null) {
			
			Optional<PlanetaEntity> planeta = repository.findById(id);
			
			return planeta.orElse(null);
		}
		
		return null;
	}
	
	public PlanetsDTO consunoSWApi(String id) throws URISyntaxException {
		
		restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
	    HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		
		final String baseUrl = PATH_PLANETS + id;
	    URI uri = new URI(baseUrl);

		ResponseEntity<PlanetsDTO> planetsResponse = restTemplate.exchange(uri, HttpMethod.GET, entity, PlanetsDTO.class);

		return planetsResponse.getBody();
	}
}
