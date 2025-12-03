package src;

/**
 * Estados possíveis do Gerente de Roteamento
 */
public enum ManagerStateEnum {
    Idle,
    LinkCostRequest,
    LinkCostSetRequest_1,
    LinkCostSetRequest_2,
    DistanceTableRequest
}
