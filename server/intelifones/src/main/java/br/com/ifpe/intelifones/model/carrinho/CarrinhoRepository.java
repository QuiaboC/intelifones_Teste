package br.com.ifpe.intelifones.model.carrinho;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.intelifones.model.usuario.Usuario;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    Optional<Carrinho> findByUsuario(Usuario usuario);

}