
OPENJDK_DIR=/home/roman/src/OpenJDK/openjdk/build/linux-i586
BOOTCLASSPATH=/home/roman/workspace/escher/build:/home/roman/src/hg/escher-peers/build
CLASSPATH=/home/roman/src/test:/home/roman/install/share/classpath/examples/examples.zip
TOOLKIT=gnu.java.awt.peer.x.XToolkit
GRAPHICSENV=gnu.java.awt.peer.x.XGraphicsEnvironment
MAIN=gnu.classpath.examples.swing.ButtonDemo

$OPENJDK_DIR/bin/java -Xbootclasspath/a:$BOOTCLASSPATH -Dawt.toolkit=$TOOLKIT -Djava.awt.graphicsenv=$GRAPHICSENV -cp $CLASSPATH $MAIN
