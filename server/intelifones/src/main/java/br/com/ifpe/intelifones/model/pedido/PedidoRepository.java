package br.com.ifpe.intelifones.model.pedido;

import br.com.ifpe.intelifones.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByComprador(Usuario comprador);

}
