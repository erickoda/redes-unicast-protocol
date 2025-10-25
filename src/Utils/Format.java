package src.Utils;

import src.Unicast.UnicastPDU;

/**
 * Implementa formatações de String
 * <p>
 * Essa classe é responsável por formatar uma String no formato padrão da
 * {@link UnicastPDU}.
 *
 * @see UnicastPDU
 */
public class Format {

    /**
     * Construtor privado para impedir a instanciação desta classe de utilidade.
     * <p>
     * Esta classe contém apenas métodos estáticos e não deve ser instanciada.
     * </p>
     */
    private Format() {
        // Impede a instanciação
        throw new IllegalStateException("Classe de utilidade (Utility class)");
    }

    /**
     * Formata uma mensagem de usuário para o formato de mensagem da PDU do Unicast
     * <p>
     * Formata uma mensagem para o padrão:
     * {@code UPDREQPDU <tamanho_dados> <dados>}
     * </p>
     * 
     * @param message - mensagem do usuário
     * 
     * @return mensagem no padrão da PDU do Unicast
     */
    public static String message(String message) {
        return "UPDREQPDU " + message.length() + " " + message;
    }
}
