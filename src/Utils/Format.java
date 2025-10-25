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
    /*
     * Formata uma mensagem de usuário para o formato de mensagem da PDU do Unicast
     * <p>
     * Formata uma mensagem para o padrão:
     * <UPDREQPDU><espaço><tamanho_dados><espaço><dados>
     * 
     * @param message - mensagem do usuário
     * 
     * @return mensagem no padrão da PDU do Unicast
     */
    public static String message(String message) {
        return "UPDREQPDU " + message.length() + " " + message;
    }
}
