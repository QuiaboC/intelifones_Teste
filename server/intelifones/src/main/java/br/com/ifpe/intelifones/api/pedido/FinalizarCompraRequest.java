package br.com.ifpe.intelifones.api.pedido;

import lombok.Data;

@Data
public class FinalizarCompraRequest {
    private String endereco;
    private String cep;
    private String numero;
    private String complemento;
    private String telefoneContato;
    private String formaPagamento;
}