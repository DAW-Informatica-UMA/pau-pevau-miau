@Entity @Data @NoArgsConstructor
public class Vicerrectorado {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String email;
}
