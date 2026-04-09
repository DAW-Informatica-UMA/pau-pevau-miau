@Entity @Data @NoArgsConstructor
public class Sede {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nombre;
    private String direccion;
    private String coordenadasGPS; // Para latitud/longitud
    
    @OneToMany(mappedBy = "sede")
    private List<Aula> aulas;
}
