package src;

/**
 * Estados possíveis do Nó de Roteamento
 */
public enum NodeStateEnum {
    /** Nó está em espera */
    Idle,

    /** Nó está propagrando seu vetor de distância */
    VectorPropagation,

    /** Nó está atualizando seu vetor de distância */
    DistanceVectorUpdate
}
