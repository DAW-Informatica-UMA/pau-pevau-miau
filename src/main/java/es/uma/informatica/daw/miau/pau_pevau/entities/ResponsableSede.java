@Entity @Data @NoArgsConstructor
public class ResponsableSede {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Embedded private NombreCompleto nombreCompleto;
    @Column(nullable = false) private String email;
    private String telefonoMovil;
    @OneToOne private Sede sede;
}
