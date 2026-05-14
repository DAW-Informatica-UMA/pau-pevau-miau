package es.uma.informatica.daw.miau.pau_pevau.clients;

import es.uma.informatica.daw.miau.pau_pevau.exceptions.CatalogoException;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.MateriaDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CatalogoClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    private Map<String, InstitutoDto> institutoByNameCache = new ConcurrentHashMap<>();
    private Map<String, MateriaDto> materiaByNameCache = new ConcurrentHashMap<>();

    public CatalogoClient(RestTemplate restTemplate, @Value("${catalogo.url:http://localhost:8080}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public InstitutoDto getInstituto(Long id) {
        if (id == null) return null;
        try {
            return restTemplate.getForObject(baseUrl + "/institutos/" + id, InstitutoDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null; 
        } catch (Exception e) {
            throw new CatalogoException("Error de conexión con el catálogo al buscar instituto por ID");
        }
    }

    public InstitutoDto buscarInstitutoPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        
        // Cache simple
        String cacheKey = nombre.toLowerCase().trim();
        if (institutoByNameCache.containsKey(cacheKey)) {
            return institutoByNameCache.get(cacheKey);
        }

        try {
            ResponseEntity<List<InstitutoDto>> response = restTemplate.exchange(
                    baseUrl + "/institutos",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<InstitutoDto>>() {}
            );
            List<InstitutoDto> institutos = response.getBody();
            if (institutos != null) {
                for (InstitutoDto instituto : institutos) {
                    institutoByNameCache.put(instituto.getNombre().toLowerCase().trim(), instituto);
                    if (nombre.equalsIgnoreCase(instituto.getNombre())) {
                        return instituto;
                    }
                }
            }
            return null;
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new CatalogoException("Error de conexión con el catálogo al buscar instituto por nombre");
        }
    }

    public MateriaDto getMateria(Long id) {
        if (id == null) return null;
        try {
            return restTemplate.getForObject(baseUrl + "/materias/" + id, MateriaDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new CatalogoException("Error de conexión con el catálogo al buscar materia por ID");
        }
    }

    public MateriaDto buscarMateriaPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        
        String cacheKey = nombre.toLowerCase().trim();
        if (materiaByNameCache.containsKey(cacheKey)) {
            return materiaByNameCache.get(cacheKey);
        }

        try {
            ResponseEntity<List<MateriaDto>> response = restTemplate.exchange(
                    baseUrl + "/materias",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<MateriaDto>>() {}
            );
            List<MateriaDto> materias = response.getBody();
            if (materias != null) {
                for (MateriaDto materia : materias) {
                    materiaByNameCache.put(materia.getNombre().toLowerCase().trim(), materia);
                    if (nombre.equalsIgnoreCase(materia.getNombre())) {
                        return materia;
                    }
                }
            }
            return null;
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new CatalogoException("Error de conexión con el catálogo al buscar materia por nombre");
        }
    }
}
