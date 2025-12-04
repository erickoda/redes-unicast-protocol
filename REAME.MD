## Run the test
Execute o arquivo .jar e escolha um ou mais comandos descritos pelo print que será exibido no terminal. Segue abaixo a lista de comandos:

- table <id>           : Solicita a Tabela de Distância do nó (Ex: table 1)
- cost <A> <B>         : Solicita o custo do link entre A e B (Ex: cost 1 2)
- set <A> <B> <custo>  : Altera o custo entre A e B (Ex: set 1 2 10)
- exit                 : Sair do programa

## Generate .jar
```bash
# Gera o .jar para o gerente
mkdir -p binManager

javac -d binManager -sourcepath src src/*.java src/Routing/*.java src/Routing/PDU/*.java src/Unicast/*.java src/Unicast/Exception/*.java

jar cvfm Manager.jar manifest_manager.txt -C binManager .

# Pode ser necessário usar sudo para rodar o .jar
java -jar Manager.jar
```

```bash
# Gera o .jar para o nó
mkdir -p binNode

javac -d binNode -sourcepath src src/*.java src/Routing/*.java src/Routing/PDU/*.java src/Unicast/*.java src/Unicast/Exception/*.java

jar cvfm Node.jar manifest_node.txt -C binNode .

# Pode ser necessário usar sudo para rodar o .jar
java -jar Node.jar
```

## Generate javadocs
```bash
javadoc -d docs -sourcepath . -subpackages src
```
