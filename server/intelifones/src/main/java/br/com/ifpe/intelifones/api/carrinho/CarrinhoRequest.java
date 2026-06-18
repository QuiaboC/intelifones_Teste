package br.com.ifpe.intelifones.api.carrinho;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarrinhoRequest {

    @NotNull
    private Long produtoId;

    @NotNull
    @Min(1)
    private Integer quantidade;
}