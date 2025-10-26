- mkdir -p bin

- javac -d bin -sourcepath src src/Main.java src/Routing/*.java src/Unicast/*.java src/Unicast/Exception/*.java

- jar cvfm RedesProjeto.jar manifest.txt -C bin .

- java -jar RedesProjeto.jar 1