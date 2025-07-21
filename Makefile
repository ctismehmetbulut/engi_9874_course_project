JFLAGS = -cp ".;sqlite-jdbc-3.8.9.1.jar"
JC = javac
JAVA = java
MAIN = Main

.PHONY: all run compile clean

all: clean compile run

compile:
	$(JC) $(JFLAGS) ${MAIN}.java

run:
	$(JAVA) -ea --enable-native-access=ALL-UNNAMED $(JFLAGS) $(MAIN)

clean:
	del /Q *.class 2> NUL
	del /Q model\*.class 2> NUL
	del /Q dao\*.class 2> NUL
	del /Q observer\*.class 2> NUL
