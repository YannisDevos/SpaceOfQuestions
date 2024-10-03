#!/bin/bash
export CLASSPATH=`find ./lib -name "*.jar" | tr '\n' ':'`
export MAINCLASS=game # <- à remplacer par le nom de votre programme
java -cp ${CLASSPATH}:classes $MAINCLASS
# Le programme s'exécute depuis la racine de l'archive
