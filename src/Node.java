package src;

import src.Routing.PDU.*;
import src.Routing.RoutingInformationProtocol;
import src.Routing.RoutingInformationProtocolStrategy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementa a estratégia de roteamento para a Entidade Nó (ID 1-15).
 * <p>
 * Esta classe é responsável por manter a tabela de distâncias, processar as
 * PDUs
 * de roteamento e executar o algoritmo de vetor de distância.
 * </p>
 */
public class Node implements RoutingInformationProtocolStrategy {

    /** Identificador único do ponto de acesso ao serviço (UCSAP) deste nó. */
    private final short ucsapId;

    /**
     * Tabela de Distância.
     * 
     * Matriz onde as linhas representam os vizinhos e as colunas os destinos.
     * 
     */
    private int[][] distanceTable;

    /** Armazena o ucsap_id dos nós vizinhos */
    private short[] neighboursUcsapId;

    /** Custo direto para os vizinhos (Topologia Física). */
    private int[] custoParaVizinho;

    /** Armazena o estado atual da máquina de estados do nó. */
    private NodeStateEnum nodeState = NodeStateEnum.Idle;

    /** Referência ao contexto do protocolo para acesso ao serviço Unicast. */
    private final RoutingInformationProtocol routingInformationProtocol;

    /**
     * Agendador para o envio periódico de RIPIND (Timeout Propagation).
     */
    private final ScheduledExecutorService scheduler;

    /** Intervalo de tempo para o broadcast periódico (padrão 10s). */
    private final int timeoutSeconds;

    /**
     * Construtor da estratégia de Nó.
     *
     * @param context        A instância principal do protocolo (Contexto).
     * @param ucsapId        O identificador deste nó (1-15).
     * @param defaultTimeout O tempo em segundos para o timeout de propagação.
     * 
     */
    public Node(RoutingInformationProtocol context, short ucsapId, int defaultTimeout) {
        this.routingInformationProtocol = context;
        this.ucsapId = ucsapId;
        this.timeoutSeconds = defaultTimeout;
        this.scheduler = Executors.newScheduledThreadPool(1);

        String confPath = "./src/data/network_configuration.txt";
        int[] distanceVector = LoadNetworkConf.loadDistanceVector(confPath, this.ucsapId);
        short[] neighboursUcsapId = LoadNetworkConf.loadNeighbourhood(confPath, this.ucsapId);
        this.neighboursUcsapId = neighboursUcsapId;

        // Carrega a tabela de distância
        this.distanceTable = new int[neighboursUcsapId.length + 1][distanceVector.length];
        this.distanceTable[0] = distanceVector;

        for (int i = 1; i < this.distanceTable.length; i++) {
            for (int j = 0; j < this.distanceTable[i].length; j++) {
                if (i == j) {
                    this.distanceTable[i][j] = 0;
                } else {
                    this.distanceTable[i][j] = -1;
                }
            }
        }

        System.out.println("Vetor de distancia nó: " + this.ucsapId);

        for (int i = 0; i < distanceVector.length; i++) {
            System.out.print(distanceVector[i] + " ");
        }

        System.out.println();

        // Carrega o custo inicial para vizinhos
        this.custoParaVizinho = new int[this.neighboursUcsapId.length];
        int counter = 0;
        for (int i = 0; i < distanceVector.length; i++) {
            if (distanceVector[i] > -1 && i != (this.ucsapId - 1)) {
                this.custoParaVizinho[counter] = distanceVector[i];
                counter++;
            }
        }

        // Inicializa o scheduler
        scheduler.scheduleAtFixedRate(this::timeoutPropagation, timeoutSeconds, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Processa mensagens recebidas da camada de Unicast.
     * Implementa as transições da máquina de estados.
     *
     * @param source  O ID da entidade que enviou a mensagem.
     * @param message A string contendo a PDU recebida.
     */
    @Override
    public void handleMessage(short source, String message) {
        if (message == null || message.isEmpty())
            return;

        String[] words = message.trim().split(" ");

        try {
            switch (words[0]) {
                case "RIPGET":
                    handleRipGet(source, message);
                    break;

                case "RIPSET":
                    handleRipSet(source, message);
                    break;

                case "RIPIND":
                    handleRipInd(message);
                    break;

                case "RIPRQT":
                    handleRipRqt(source, message);
                    break;

                default:
                    System.err.println("[ERRO] Tipo de Mensagem Inválida ou não suportada pelo Nó: " + words[0]);
                    break;
            }
        } catch (InvalidRIPPDUException e) {
            e.printStackTrace();
            // Garante que o nó não fique travado em um estado transiente em caso de erro
            this.nodeState = NodeStateEnum.Idle;
        }
    }

    /**
     * Trata a recepção de uma PDU RIPGET (Solicitação de Custo).
     * 
     * @param source  O ID da entidade que enviou a mensagem.
     * @param message A string contendo a PDU recebida.
     * 
     * @throws InvalidRIPPDUException Se a PDU for inválida.
     */
    private void handleRipGet(short source, String message) throws InvalidRIPPDUException {
        if (nodeState == NodeStateEnum.Idle) {
            RoutingInformationProtocolGetPDU receivedPDU = new RoutingInformationProtocolGetPDU(message);

            // Inicializa o custo como neg inf
            int cost = getCustoEnlace(receivedPDU.getRipNodeB());

            RoutingInformationProtocolNotificationPDU sendPDU = new RoutingInformationProtocolNotificationPDU(
                    receivedPDU.getRipNodeA(), receivedPDU.getRipNodeB(), cost);

            this.routingInformationProtocol.getUnicastService().UPDataReq(source, sendPDU.getMessage());
        }
    }

    /**
     * Trata a recepção de uma PDU RIPSET (Definição de Custo).
     * 
     * @param source  O ID da entidade que enviou a mensagem.
     * @param message A string contendo a PDU recebida.
     * 
     * @throws InvalidRIPPDUException Se a PDU for inválida.
     */
    private void handleRipSet(short source, String message) throws InvalidRIPPDUException {
        if (this.nodeState == NodeStateEnum.Idle) {

            RoutingInformationProtocolSetPDU receivedPDU = new RoutingInformationProtocolSetPDU(message);

            int newCost = receivedPDU.getCost();
            int currentCost = getCustoEnlace(receivedPDU.getRipNodeB());

            if (currentCost == -1 && newCost != -1) {
                System.out.println("[ERRO] Tentativa de criar enlace inexistente. Custo atual: " + currentCost
                        + ", Novo Custo: " + newCost);
                this.nodeState = NodeStateEnum.Idle;
                return;
            }

            this.nodeState = NodeStateEnum.DistanceVectorUpdate;

            this.updateCustoEnlace(receivedPDU.getRipNodeB(), newCost);

            // Envia confirmação (RIPNTF)
            RoutingInformationProtocolNotificationPDU sendPDU = new RoutingInformationProtocolNotificationPDU(
                    receivedPDU.getRipNodeA(), receivedPDU.getRipNodeB(), newCost);

            this.routingInformationProtocol.getUnicastService().UPDataReq(source, sendPDU.getMessage());

            if (newCost == currentCost) {
                this.nodeState = NodeStateEnum.Idle;
                return;
            }

            if (this.recalculateDistaceVector()) {
                this.propagateDistanceVector();
            }

            this.nodeState = NodeStateEnum.Idle;
        }
    }

    /**
     * Trata a recepção de uma PDU RIPIND (Vetor de Distância de Vizinho).
     * 
     * @param message A string contendo a PDU recebida.
     * 
     * @throws InvalidRIPPDUException Se a PDU for inválida.
     */
    private void handleRipInd(String message) throws InvalidRIPPDUException {
        if (this.nodeState == NodeStateEnum.Idle) {

            // Transição para estado de atualização
            this.nodeState = NodeStateEnum.DistanceVectorUpdate;

            RoutingInformationProtocolIndicationPDU receivedPDU = new RoutingInformationProtocolIndicationPDU(message);

            // Pega o index do ucsap_id do nó
            int distanceTableIndex = getNeighborRowIndex(receivedPDU.getRipNode());

            if (distanceTableIndex == -1) {
                System.err.println("[ERRO]: Nó origem não é vizinho do nó atual");
                this.nodeState = NodeStateEnum.Idle;
                return;
            }

            this.distanceTable[distanceTableIndex] = receivedPDU.getDistanceVector();

            if (this.recalculateDistaceVector()) {
                this.propagateDistanceVector();
            }

            this.nodeState = NodeStateEnum.Idle;

        }
    }

    /**
     * Trata a recepção de uma PDU RIPRQT (Solicitação de Tabela).
     * 
     * @param source  O ID da entidade que enviou a mensagem.
     * @param message A string contendo a PDU recebida.
     * 
     * @throws InvalidRIPPDUException Se a PDU for inválida.
     */
    private void handleRipRqt(short source, String message) throws InvalidRIPPDUException {
        if (nodeState == NodeStateEnum.Idle) {
            RoutingInformationProtocolRequestPDU receivedPDU = new RoutingInformationProtocolRequestPDU(message);
            short manager = 0;

            // Monta resposta com a tabela completa
            RoutingInformationProtocolResponsePDU sendPDU = new RoutingInformationProtocolResponsePDU(
                    manager, this.distanceTable);

            this.routingInformationProtocol.getUnicastService().UPDataReq(source, sendPDU.getMessage());
        }
    }

    /**
     * Recalcula o vetor de distância do nó.
     * 
     * @return true se o vetor de distância foi alterado, false caso contrário.
     */
    private boolean recalculateDistaceVector() {
        boolean hasChanged = false;
        int vectorSize = this.distanceTable[0].length;

        for (int j = 0; j < vectorSize; j++) {

            // Pula o cálculo para si mesmo
            if (j == (this.ucsapId - 1)) {
                continue;
            }

            int menorCaminhoEncontrado = -1; // Começamos assumindo Infinito

            for (int i = 1; i < this.distanceTable.length; i++) {

                int neighbourUcsapId = this.neighboursUcsapId[i - 1];
                int custoEnlace = this.getCustoEnlace(neighbourUcsapId);
                int distanciaVizinhoAteDestino = this.distanceTable[i][j];

                if (custoEnlace > -1 && distanciaVizinhoAteDestino > -1) {

                    int custoTotal = custoEnlace + distanciaVizinhoAteDestino;

                    if (custoTotal > 15)
                        custoTotal = -1;

                    if (custoTotal > -1) {
                        if (menorCaminhoEncontrado == -1 || custoTotal < menorCaminhoEncontrado) {
                            menorCaminhoEncontrado = custoTotal;
                        }
                    }
                }
            }

            if (this.distanceTable[0][j] != menorCaminhoEncontrado) {
                this.distanceTable[0][j] = menorCaminhoEncontrado;
                hasChanged = true;
            }
        }

        return hasChanged;
    }

    /**
     * Retorna o custo do enlace para um nó vizinho específico.
     * 
     * @param nodeUcsapId O ID UCSAP do nó vizinho.
     * 
     * @return O custo do enlace ou -1 se o nó não for vizinho.
     */
    private int getCustoEnlace(int nodeUcsapId) {
        for (int i = 0; i < this.neighboursUcsapId.length; i++) {
            if (nodeUcsapId == this.neighboursUcsapId[i]) {
                return this.custoParaVizinho[i];
            }
        }

        return -1;
    }

    /**
     * Propaga o vetor de distância para seus vizinhos
     */
    private void propagateDistanceVector() {
        for (short neighbourUcsapId : neighboursUcsapId) {
            RoutingInformationProtocolIndicationPDU ripIndicationPDU = new RoutingInformationProtocolIndicationPDU(
                    this.ucsapId, this.distanceTable[0]);

            this.routingInformationProtocol.getUnicastService().UPDataReq(neighbourUcsapId,
                    ripIndicationPDU.getMessage());
        }
    }

    /**
     * Altera o custo do enlace
     * 
     * @param nodeId  o Id do nó vizinho cujo custo foi alterado
     * @param newCost o novo custo
     */
    private void updateCustoEnlace(int nodeId, int newCost) {
        for (int i = 0; i < neighboursUcsapId.length; i++) {
            if (this.neighboursUcsapId[i] == nodeId) {
                this.custoParaVizinho[i] = newCost;
                return;
            }
        }
    }

    /**
     * Timeout para atualizar vetores de distância
     */
    private void timeoutPropagation() {
        if (this.nodeState == NodeStateEnum.Idle) {
            this.nodeState = NodeStateEnum.VectorPropagation;

            this.propagateDistanceVector();

            this.nodeState = NodeStateEnum.Idle;
        }
    }

    /**
     * Calcula o index do nó vizinho
     * 
     * @param nodeId id do nó vizinho
     * @return
     */
    private int getNeighborRowIndex(int nodeId) {
        for (int i = 0; i < neighboursUcsapId.length; i++) {
            if (neighboursUcsapId[i] == nodeId)
                return i + 1;
        }
        return -1;
    }
}