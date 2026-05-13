package es.uma.informatica.daw.miau.pau_pevau.clients;

import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.MateriaDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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
            InstitutoDto[] institutos = restTemplate.getForObject(baseUrl + "/institutos?nombre=" + nombre, InstitutoDto[].class);
            if (institutos != null && institutos.length > 0) {
                return institutos[0];
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
}
