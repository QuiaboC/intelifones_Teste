package br.com.ifpe.intelifones.model.produto;

import br.com.ifpe.intelifones.model.categoria.Categoria;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, length = 100)
   private String nome;

   @Column(length = 500)
   private String descricao;

   @Column(nullable = false)
   private Double preco;

   @Column(nullable = false)
   private Integer quantidade;

   private Boolean usado;

   @Column(nullable = false)
   private Boolean ativo = true;

   @Column(name = "imagem")
   private String imagem;

   @ManyToOne
   @JoinColumn(name = "categoria_id")
   private Categoria categoria;

   @ManyToOne
   @JoinColumn(name = "vendedor_id", nullable = false)
   private Usuario vendedor;
}