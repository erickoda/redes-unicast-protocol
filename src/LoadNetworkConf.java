package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class LoadNetworkConf {

    /** Constante para indicar distância infinita */
    public static final int INFINITY = -1;

    /** Quantidade máxima de nós na rede */
    public static final int MAX_NODES = 15;

    /**
     * Extrai os IDs dos vizinhos diretos de um nó.
     * <p>
     * Este método lê o arquivo de topologia (usando o caminho padrão) para obter
     * a matriz de adjacência atualizada e então filtra os vizinhos do nó
     * solicitado.
     * </p>
     * * @param nodeId O id do nó (formato do protocolo, 1 a 15).
     * 
     * @return short[] Um array contendo apenas os IDs dos vizinhos diretos.
     */
    public static short[] loadNeighbourhood(String filePath, short nodeId) {
        int[][] matrix = loadTopology(filePath);

        ArrayList<Short> neighborList = new ArrayList<Short>();

        // CONVERSÃO: O ID do protocolo (1..15) vira índice da matriz (0..14)
        int rowIndex = nodeId - 1;

        if (rowIndex < 0 || rowIndex >= matrix.length) {
            return new short[0];
        }

        for (int colIndex = 0; colIndex < matrix.length; colIndex++) {

            int cost = matrix[rowIndex][colIndex];

            if (rowIndex != colIndex && cost != INFINITY && cost >= 0) {
                // Conversão Inversa: O índice da coluna (0..14) vira ID do vizinho (1..15)
                neighborList.add((short) (colIndex + 1));
            }
        }

        // Ordena os vizinhos em ordem crescente
        Collections.sort(neighborList);

        // Conversão de List<Short> PARA short[]
        short[] result = new short[neighborList.size()];
        for (int i = 0; i < neighborList.size(); i++) {
            result[i] = neighborList.get(i);
        }

        return result;
    }

    /**
     * Lê o arquivo de topologia e retorna o vetor de distância do nó.
     * O índice da matriz é ucsap_id - 1, então o nó 1, está representado pelo
     * índice 0.
     * 
     * @param filePath Caminho do arquivo.
     * @param ucsapId  ucsap_id do nó.
     * 
     * @return int[] Vetor de distância.
     */
    public static int[] loadDistanceVector(String filePath, short ucsapId) {
        int[][] topology = loadTopology(filePath);
        short maxIdFound = (short) topology.length;

        if (ucsapId > maxIdFound) {
            System.err.println("[ERRO]: Nó " + ucsapId + " inválido");
            System.exit(1);
        }

        int[] finalMatrix = topology[ucsapId - 1];

        return finalMatrix;
    }

    /**
     * Lê o arquivo de topologia e retorna uma matriz ajustada dinamicamente.
     * O índice da matriz é ucsap_id - 1, então o nó 1, está representado pelo
     * índice 0.
     * * @param filePath Caminho do arquivo.
     * * @return int[][] Matriz com a distância até os vizinhos.
     */
    public static int[][] loadTopology(String filePath) {
        // Cria uma matriz temporária para leitura
        int[][] tempMatrix = new int[MAX_NODES][MAX_NODES];

        // Inicializa com INFINITY e 0 na diagonal
        for (int i = 0; i < MAX_NODES; i++) {
            for (int j = 0; j < MAX_NODES; j++) {
                if (i == j)
                    tempMatrix[i][j] = 0;
                else
                    tempMatrix[i][j] = INFINITY;
            }
        }

        // Rastreia o maior nó real da rede
        int maxIdFound = 0;
        File configFile = new File(filePath);

        try (Scanner sc = new Scanner(configFile)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty())
                    continue;

                String[] parts = line.split("\\s+");

                if (parts.length >= 3) {
                    try {
                        int nodeA = Integer.parseInt(parts[0]);
                        int nodeB = Integer.parseInt(parts[1]);
                        int cost = Integer.parseInt(parts[2]);

                        if (isValidNodeId(nodeA) && isValidNodeId(nodeB)) {

                            tempMatrix[nodeA - 1][nodeB - 1] = cost;
                            tempMatrix[nodeB - 1][nodeA - 1] = cost;

                            // Atualiza o "corte" da matriz
                            maxIdFound = Math.max(maxIdFound, Math.max(nodeA, nodeB));
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("[AVISO] Erro de formatação na linha: " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("[ERRO] Arquivo não encontrado: " + filePath);
            System.exit(1);
        }

        // Cria a Matriz Final "Recortada"
        int finalSize = maxIdFound;
        int[][] finalMatrix = new int[finalSize][finalSize];

        for (int i = 0; i < finalSize; i++) {
            for (int j = 0; j < finalSize; j++) {
                finalMatrix[i][j] = tempMatrix[i][j];
            }
        }

        return finalMatrix;
    }

    /**
     * Valida se o ID é um nó de roteamento válido.
     * Nós devem ser > 0 (pois 0 é gerente) e <= 15.
     * 
     * @param id ucsap_id do nó
     * 
     * @return boolean, se o número do nó é válido
     */
    private static boolean isValidNodeId(int id) {
        return id > 0 && id <= MAX_NODES;
    }
}