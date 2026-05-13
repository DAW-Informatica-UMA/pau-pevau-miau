package es.uma.informatica.daw.miau.pau_pevau.clients;

import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.MateriaDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class CatalogoClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CatalogoClient(RestTemplate restTemplate, @Value("${catalogo.url:http://localhost:8080}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    // Hemos añadido SERVICE_UNAVAILABLE para indicar que el error viene de un servicio externo, no de nuestra lógica.
    // Los errores 404 permite a nuestro codigo decidir que hacer.

    public InstitutoDto getInstituto(Long id) {
        if (id == null) return null;
        try {
            return restTemplate.getForObject(baseUrl + "/institutos/" + id, InstitutoDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null; 
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error de conexión con el catálogo al buscar instituto por ID", e);
        }
    }

    public InstitutoDto buscarInstitutoPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
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
                    if (nombre.equalsIgnoreCase(instituto.getNombre())) {
                        return instituto;
                    }
                }
            }
            return null;
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error de conexión con el catálogo al buscar instituto por nombre", e);
        }
    }

    public MateriaDto getMateria(Long id) {
        if (id == null) return null;
        try {
            return restTemplate.getForObject(baseUrl + "/materias/" + id, MateriaDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error de conexión con el catálogo al buscar materia por ID", e);
        }
    }

    public MateriaDto buscarMateriaPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
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
                    if (nombre.equalsIgnoreCase(materia.getNombre())) {
                        return materia;
                    }
                }
            }
            return null;
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error de conexión con el catálogo al buscar materia por nombre", e);
        }
    }
}
