## Run the test
Para executar o teste, será necessário executar duas instâncias da Main.java ou o .jar equivalente (RedesProjeto.jar). Cada instância executará um nó passado por linha de comando:

```bash
# Primeiro Nó
java -jar RedesProjeto.jar 1
```

```bash
# Segundo Nó
java -jar RedesProjeto.jar 2
```

Em seguida, é possível enviar mensagens pelo seguinte comando:
```bash
# send <número_do_nó> <mensagem>
send 2 Hello, Node 2!
```

Ainda, o comando 'read' pode ser usado para ler o histórico de mensagens recebidas
```bash
read
```

Por fim, ao digitar o comando 'exit', o nó irá parar sua execução
```bash
exit
```

## Generate .jar
```bash
mkdir -p bin

javac -d bin -sourcepath src src/Main.java src/Routing/*.java src/Unicast/*.java src/Unicast/Exception/*.java

jar cvfm RedesProjeto.jar manifest.txt -C bin .

java -jar RedesProjeto.jar 1
```

## Generate javadocs
```bash
javadoc -d docs -sourcepath . -subpackages src
```
