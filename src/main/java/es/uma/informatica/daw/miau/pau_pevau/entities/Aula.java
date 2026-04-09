@Entity @Data @NoArgsConstructor
public class Aula {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer aforo;
    private String disponibilidad;
    @ManyToOne private Sede sede;
}
