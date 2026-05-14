package es.uma.informatica.daw.miau.pau_pevau.clients;

import es.uma.informatica.daw.miau.pau_pevau.models.ConvocatoriaDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CalendarioClient {

    private final RestTemplate restTemplate;
    private final String calendarioServiceUrl;

    public CalendarioClient(RestTemplate restTemplate, @Value("${microservicio.calendario.url:http://localhost:8082}") String calendarioServiceUrl) {
        this.restTemplate = restTemplate;
        this.calendarioServiceUrl = calendarioServiceUrl;
    }

    public Long obtenerConvocatoriaActualId() {
        try {
            String url = calendarioServiceUrl + "/convocatorias/actual";
            ConvocatoriaDto convocatoria = restTemplate.getForObject(url, ConvocatoriaDto.class);
            return (convocatoria != null && convocatoria.getId() != null) ? convocatoria.getId() : 2L;
        } catch (Exception e) {
            System.out.println("Aviso: Microservicio de Calendario no disponible. Usando convocatoria por defecto (2L).");
            return 2L;
        }
    }
}