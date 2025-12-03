package src;

/**
 * Estados possíveis do Gerente de Roteamento
 */
public enum ManagerStateEnum {
    /** Nó está aguardando */
    Idle,

    /** Requisição de custo de enlace */
    LinkCostRequest,

    /** Primeira Etapa da requisição de definição de custo de enlace */
    LinkCostSetRequest_1,

    /** Segunda etapa da requisição de definição de custo de enlace */
    LinkCostSetRequest_2,

    /** Requisição de Tabela de Distância */
    DistanceTableRequest
}
