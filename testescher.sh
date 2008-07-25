OPENJDK_DIR=/home/neugens/work_space/Eclipse/openjdk/openjdk/build/linux-i586/
BOOTCLASSPATH=/home/neugens/work_space/Eclipse/escher/dist/escher-0.3.jar:/home/neugens/work_space/Eclipse/caciocavallo-ng/dist/echer-peer.jar
#CLASSPATH=/home/neugens/work_space/Eclipse/classpath/examples/examples.zip
CLASSPATH=/home/neugens/work_space/Eclipse/caciocavallo/build/test/
TOOLKIT=gnu.java.awt.peer.x.XToolkit
GRAPHICSENV=gnu.java.awt.peer.x.XGraphicsEnvironment
#MAIN=gnu.classpath.examples.swing.Demo
MAIN=gnu.escher.TestButton

echo "$OPENJDK_DIR/bin/java -Xbootclasspath/a:$BOOTCLASSPATH -Dawt.toolkit=$TOOLKIT -Djava.awt.graphicsenv=$GRAPHICSENV -cp $CLASSPATH $MAIN" 

#Xephyr :2 -ac -screen 1430x850 & 

#metacity --display :2 &
#DISPLAY=:2
$OPENJDK_DIR/bin/java -Xdebug -Xnoagent -Xbootclasspath/a:$BOOTCLASSPATH -Dawt.toolkit=$TOOLKIT -Dsun.font.fontmanager=gnu.java.awt.peer.x.EscherFontManager -Djava.awt.graphicsenv=$GRAPHICSENV  -cp $CLASSPATH $MAIN
