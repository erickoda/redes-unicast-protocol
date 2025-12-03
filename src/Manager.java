package src;

import src.Routing.RoutingInformationProtocol;
import src.Routing.RoutingInformationProtocolStrategy;
import src.Routing.PDU.RoutingInformationProtocolGetPDU;
import src.Routing.PDU.RoutingInformationProtocolRequestPDU;
import src.Routing.PDU.RoutingInformationProtocolSetPDU;
import src.Routing.PDU.RoutingInformationProtocolResponsePDU;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Implementa a estratégia de roteamento para a Entidade Gerente (ID 0).
 * <p>
 * Responsável por gerenciar a máquina de estados, controlando
 * timeouts de retransmissão e transições de requisições.
 * </p>
 */
public class Manager implements RoutingInformationProtocolStrategy {

    /** Referência ao contexto para acessar Unicast e Callbacks */
    private final RoutingInformationProtocol context;

    /** Executor para gerenciar os timers */
    private final ScheduledExecutorService scheduler;

    /** Referência para a tarefa de retransmissão atual */
    private ScheduledFuture<?> retransmissionTask;

    /** Configuração de timeout */
    private final int TIMEOUT_SECONDS;

    /** Estado atual do Gerente */
    private ManagerStateEnum currentState = ManagerStateEnum.Idle;

    // --- Variáveis temporárias para controle de fluxo e retransmissão ---
    private short targetNode; // Nó de quem esperamos resposta
    private String lastPduSent; // Última mensagem enviada (para retransmissão)

    // Variáveis específicas para a transição complexa de SetLinkCost
    private short tempNodeA;
    private short tempNodeB;
    private int tempCost;

    /**
     * Construtor da estratégia do Gerente.
     * 
     * @param context        Contexto principal do protocolo.
     * @param timeoutSeconds Tempo em segundos para retransmissão.
     */
    public Manager(RoutingInformationProtocol context, int timeoutSeconds) {
        this.context = context;
        this.TIMEOUT_SECONDS = timeoutSeconds;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Processa mensagens recebidas (RIPNTF, RIPRSP) conforme máquina de estados.
     */
    @Override
    public void handleMessage(short source, String message) {
        if (currentState == ManagerStateEnum.Idle)
            return;

        String[] words = message.trim().split(" ");
        String type = words[0];

        switch (type) {
            case "RIPNTF":
                handleRipNtf(source, words);
                break;
            case "RIPRSP":
                handleRipRsp(source, message);
                break;
            default:
                break;
        }
    }

    private void handleRipNtf(short source, String[] words) {
        // Validação de segurança: só aceita se vier do nó que estamos aguardando
        if (source != targetNode)
            return;

        // Transição: LinkCostRequest -> Idle
        if (currentState == ManagerStateEnum.LinkCostRequest) {
            stopRetransmissionTimer();
            currentState = ManagerStateEnum.Idle;

            // Notifica aplicação: linkCostIndication(nodeA, nodeB, cost)
            context.notifyLinkCostIndication(
                    Short.parseShort(words[1]),
                    Short.parseShort(words[2]),
                    Integer.parseInt(words[3]));
        }
        // Transição: LinkCostSetRequest_1 -> LinkCostSetRequest_2
        else if (currentState == ManagerStateEnum.LinkCostSetRequest_1) {
            stopRetransmissionTimer();

            // Recebemos OK do Nó A. Agora precisamos enviar para o Nó B.
            // Usamos os dados temporários guardados no executeSetLinkCost
            this.targetNode = tempNodeB;

            // Cria a PDU para o Nó B: RIPSET B A Cost
            RoutingInformationProtocolSetPDU nextPDU = new RoutingInformationProtocolSetPDU(tempNodeB, tempNodeA,
                    tempCost);
            this.lastPduSent = nextPDU.getMessage(); // Assumindo método getMessage() ou getFrame()

            // Avança estado e inicia novo ciclo de envio/timer
            currentState = ManagerStateEnum.LinkCostSetRequest_2;
            sendAndStartTimer(targetNode, lastPduSent);
        }
        // Transição: LinkCostSetRequest_2 -> Idle
        else if (currentState == ManagerStateEnum.LinkCostSetRequest_2) {
            stopRetransmissionTimer();
            currentState = ManagerStateEnum.Idle;

            // Notifica aplicação que a operação completa (A e B) terminou
            context.notifyLinkCostIndication(tempNodeA, tempNodeB, tempCost);
        }
    }

    private void handleRipRsp(short source, String message) {
        if (source != targetNode)
            return;

        // Transição: DistanceTableRequest -> Idle
        if (currentState == ManagerStateEnum.DistanceTableRequest) {
            stopRetransmissionTimer();
            currentState = ManagerStateEnum.Idle;

            // Parse da PDU de resposta
            RoutingInformationProtocolResponsePDU pdu = new RoutingInformationProtocolResponsePDU(message);

            // Notifica a aplicação passando a tabela parseada
            // (Assumindo que getDistanceTable() retorna int[][])
            // Se sua PDU retorna outro formato, ajuste aqui.
            context.notifyDistanceTableIndication(source, pdu.getDistanceTable());
        }
    }

    @Override
    public boolean executeGetLinkCost(short nodeA, short nodeB) {
        if (currentState != ManagerStateEnum.Idle) {
            System.err.println("[ERRO] Gerente ocupado processando outra requisição.");
            return false;
        }

        this.targetNode = nodeA;
        RoutingInformationProtocolGetPDU pdu = new RoutingInformationProtocolGetPDU(nodeA, nodeB);
        this.lastPduSent = pdu.getMessage();

        currentState = ManagerStateEnum.LinkCostRequest;
        sendAndStartTimer(targetNode, lastPduSent);
        return true;
    }

    @Override
    public boolean executeSetLinkCost(short nodeA, short nodeB, int cost) {
        if (currentState != ManagerStateEnum.Idle) {
            System.err.println("[ERRO] Gerente ocupado processando outra requisição.");
            return false;
        }

        // Armazena dados necessários para a segunda etapa (quando formos enviar para B)
        this.tempNodeA = nodeA;
        this.tempNodeB = nodeB;
        this.tempCost = cost;

        // Começa enviando para A
        this.targetNode = nodeA;
        RoutingInformationProtocolSetPDU pdu = new RoutingInformationProtocolSetPDU(nodeA, nodeB, cost);
        this.lastPduSent = pdu.getMessage();

        currentState = ManagerStateEnum.LinkCostSetRequest_1;
        sendAndStartTimer(targetNode, lastPduSent);
        return true;
    }

    @Override
    public boolean executeGetDistanceTable(short node) {
        if (currentState != ManagerStateEnum.Idle) {
            System.err.println("[ERRO] Gerente ocupado processando outra requisição.");
            return false;
        }

        this.targetNode = node;
        RoutingInformationProtocolRequestPDU pdu = new RoutingInformationProtocolRequestPDU();
        this.lastPduSent = pdu.getMessage();

        currentState = ManagerStateEnum.DistanceTableRequest;
        sendAndStartTimer(targetNode, lastPduSent);
        return true;
    }

    /**
     * Envia a mensagem via Unicast e inicia o timer de retransmissão.
     */
    private void sendAndStartTimer(short dest, String msg) {
        // Envia a primeira vez
        context.getUnicastService().UPDataReq(dest, msg);

        // Agenda a retransmissão caso não haja resposta
        startRetransmissionTimer(dest, msg);
    }

    private void startRetransmissionTimer(short node, String pdu) {
        // Cancela timer anterior para evitar duplicidade
        stopRetransmissionTimer();

        retransmissionTask = scheduler.schedule(() -> {
            System.out.println("[TIMEOUT MANAGER] Sem resposta. Reenviando para " + node + ": " + pdu);

            // Reenvia
            context.getUnicastService().UPDataReq(node, pdu);

            // Reagendar recursivamente
            startRetransmissionTimer(node, pdu);

        }, TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private void stopRetransmissionTimer() {
        if (retransmissionTask != null && !retransmissionTask.isDone()) {
            retransmissionTask.cancel(false);
        }
    }
}