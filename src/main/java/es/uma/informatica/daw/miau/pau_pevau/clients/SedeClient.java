package es.uma.informatica.daw.miau.pau_pevau.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SedeClient {

    private final RestTemplate restTemplate;
    private final String sedeServiceUrl;

    public SedeClient(RestTemplate restTemplate, @Value("${microservicio.sedes.url:http://localhost:8081}") String sedeServiceUrl) {
        this.restTemplate = restTemplate;
        this.sedeServiceUrl = sedeServiceUrl;
    }

    public boolean existeSede(Long idSede) {
        if (idSede == null) return false;
        try {
            String url = sedeServiceUrl + "/sedes/" + idSede;
            restTemplate.getForEntity(url, Object.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            System.out.println("Aviso: Microservicio de Sedes no disponible. Ignorando validación.");
            return true;
        }
    }

    public Set<Long> obtenerTodosLosIdsSedes() {
        try {
            String url = sedeServiceUrl + "/sedes";
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    }
            );
            assert response.getBody() != null;
            return response.getBody().stream()
                    .map(s -> Long.valueOf(s.get("id").toString()))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            System.out.println("Aviso: Microservicio de Sedes no disponible. Permitiendo sede 1L por defecto.");
            return new HashSet<>(List.of(1L));
        }
    }
}