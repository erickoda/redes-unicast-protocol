package src;

import src.Routing.RoutingInformationProtocol;
import src.Routing.RoutingInformationProtocolStrategy;
import src.Routing.PDU.InvalidRIPPDUException;
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

    /** Referência ao rip para acessar Unicast */
    private final RoutingInformationProtocol rip;

    /** Executor para gerenciar os timers */
    private final ScheduledExecutorService scheduler;

    /** Referência para a tarefa de retransmissão atual */
    private ScheduledFuture<?> retransmissionTask;

    /** Configuração de timeout */
    private final int TIMEOUT_SECONDS;

    /** Estado atual do Gerente */
    private ManagerStateEnum currentState = ManagerStateEnum.Idle;

    // --- Variáveis temporárias para controle de fluxo e retransmissão ---
    /** Nó de quem esperamos resposta */
    private short targetNode;
    /** Última mensagem enviada (para retransmissão) */
    private String lastPduSent;

    // Variáveis específicas para a transição SetLinkCost
    private short tempNodeA;
    private short tempNodeB;
    private int tempCost;

    /**
     * Construtor da estratégia do Gerente.
     * 
     * @param rip            Routing Information Protocol.
     * @param timeoutSeconds Tempo em segundos para retransmissão.
     */
    public Manager(RoutingInformationProtocol rip, int timeoutSeconds) {
        this.rip = rip;
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

    /**
     * Manipula mensagens RIPNTF recebidas.
     * 
     * @param source
     * @param words
     */
    private void handleRipNtf(short source, String[] words) {
        // Validação: só aceita se vier do nó que estamos aguardando
        if (source != targetNode)
            return;

        // Transição: LinkCostRequest -> Idle
        if (currentState == ManagerStateEnum.LinkCostRequest) {
            stopRetransmissionTimer();
            currentState = ManagerStateEnum.Idle;

            // Notifica aplicação
            for (int i = 0; i < words.length; i++)
                System.out.print(words[i] + " ");
            System.out.println();
            rip.notifyLinkCostIndication(
                    Short.parseShort(words[1]),
                    Short.parseShort(words[2]),
                    Integer.parseInt(words[3]));
        }
        // Transição: LinkCostSetRequest_1 -> LinkCostSetRequest_2
        else if (currentState == ManagerStateEnum.LinkCostSetRequest_1) {
            try {
                stopRetransmissionTimer();

                this.targetNode = tempNodeB;

                RoutingInformationProtocolSetPDU nextPDU = new RoutingInformationProtocolSetPDU(tempNodeB, tempNodeA,
                        tempCost);
                this.lastPduSent = nextPDU.getMessage();

                currentState = ManagerStateEnum.LinkCostSetRequest_2;
                sendAndStartTimer(targetNode, lastPduSent);
            } catch (InvalidRIPPDUException e) {
                System.err.println("[ERRO] Falha ao criar PDU de configuração de custo de link (etapa 2).");
            }

        }
        // Transição: LinkCostSetRequest_2 -> Idle
        else if (currentState == ManagerStateEnum.LinkCostSetRequest_2) {
            stopRetransmissionTimer();
            currentState = ManagerStateEnum.Idle;

            // Notifica aplicação que a operação completa (A e B) terminou
            rip.notifyLinkCostIndication(tempNodeA, tempNodeB, tempCost);
        }
    }

    /**
     * Manipula mensagens RIPRSP recebidas.
     * 
     * @param source
     * @param message
     */
    private void handleRipRsp(short source, String message) {
        if (source != targetNode)
            return;

        // Transição: DistanceTableRequest -> Idle
        if (currentState == ManagerStateEnum.DistanceTableRequest) {
            try {
                stopRetransmissionTimer();
                currentState = ManagerStateEnum.Idle;

                RoutingInformationProtocolResponsePDU pdu = new RoutingInformationProtocolResponsePDU(message);

                // Notifica a aplicação passando a tabela de distâncias
                rip.notifyDistanceTableIndication(source, pdu.getDistanceTable());
            } catch (InvalidRIPPDUException e) {
                System.err.println("[ERRO] PDU RIPRSP inválida recebida do nó " + source);
            }
        }
    }

    @Override
    public boolean executeGetLinkCost(short nodeA, short nodeB) {
        if (currentState != ManagerStateEnum.Idle) {
            System.err.println("[ERRO] Gerente ocupado processando outra requisição.");
            return false;
        }

        try {
            this.targetNode = nodeA;
            RoutingInformationProtocolGetPDU pdu = new RoutingInformationProtocolGetPDU(nodeA, nodeB);
            this.lastPduSent = pdu.getMessage();

            currentState = ManagerStateEnum.LinkCostRequest;
            sendAndStartTimer(targetNode, lastPduSent);
            return true;
        } catch (InvalidRIPPDUException e) {
            System.err.println("[ERRO] Falha ao criar PDU de requisição de custo de link.");
            return false;
        }

    }

    @Override
    public boolean executeSetLinkCost(short nodeA, short nodeB, int cost) {
        if (currentState != ManagerStateEnum.Idle) {
            System.err.println("[ERRO] Gerente ocupado processando outra requisição.");
            return false;
        }

        try {
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
        } catch (InvalidRIPPDUException e) {
            System.err.println("[ERRO] Falha ao criar PDU de configuração de custo de link.");
            return false;
        }

    }

    @Override
    public boolean executeGetDistanceTable(short node) {
        if (currentState != ManagerStateEnum.Idle) {
            System.err.println("[ERRO] Gerente ocupado processando outra requisição.");
            return false;
        }

        try {
            this.targetNode = node;
            RoutingInformationProtocolRequestPDU pdu = new RoutingInformationProtocolRequestPDU();
            this.lastPduSent = pdu.getMessage();

            currentState = ManagerStateEnum.DistanceTableRequest;
            sendAndStartTimer(targetNode, lastPduSent);
            return true;
        } catch (InvalidRIPPDUException e) {
            System.err.println("[ERRO] Falha ao criar PDU de requisição de tabela de distâncias.");
            return false;
        }
    }

    /**
     * Envia a mensagem via Unicast e inicia o timer de retransmissão.
     * 
     * @param dest nó destino
     * @param msg  mensagem a ser enviada
     */
    private void sendAndStartTimer(short dest, String msg) {
        // Envia a primeira vez
        rip.getUnicastService().UPDataReq(dest, msg);

        // Agenda a retransmissão caso não haja resposta
        startRetransmissionTimer(dest, msg);
    }

    /**
     * Inicia o timer de retransmissão.
     * 
     * @param node nó destino
     * @param pdu  mensagem PDU enviada
     */
    private void startRetransmissionTimer(short node, String pdu) {
        // Cancela timer anterior para evitar duplicidade
        stopRetransmissionTimer();

        retransmissionTask = scheduler.schedule(() -> {
            System.out.println("[TIMEOUT MANAGER] Sem resposta. Reenviando para " + node + ": " + pdu);

            // Reenvia
            rip.getUnicastService().UPDataReq(node, pdu);

            // Reagendar recursivamente
            startRetransmissionTimer(node, pdu);

        }, TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Para o timer de retransmissão.
     */
    private void stopRetransmissionTimer() {
        if (retransmissionTask != null && !retransmissionTask.isDone()) {
            retransmissionTask.cancel(false);
        }
    }
}