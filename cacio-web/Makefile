CC         = gcc
JAVAH      = javah
MYCFLAGS   = -c -Wall -fPIC
MYLDFLAGS  = -fPIC -shared
COPY       = cp -a

CLASSPATH  = target/classes:../cacio-shared/target/classes
SRC_DIR    = src/main/c
TARGET_DIR = target

INCLUDE    = -I $(TARGET_DIR) -I $(SRC_DIR) -I $(JAVA_HOME)/include/ -I $(JAVA_HOME)/include/linux
SOURCES    = ./src/main/c/WebSurfaceData.c
TARGET     = $(TARGET_DIR)/WebSurfaceData.c
OBJECTS    = $(TARGET:.c=.o)

EXECUTABLE = libcacioweb.so

.PHONY:
JNI_LIST = net.java.openjdk.awt.peer.web.WebGraphicsEnvironment \
           net.java.openjdk.awt.peer.web.WebScreen \
           net.java.openjdk.awt.peer.web.WebSurfaceData \
           net.java.openjdk.awt.peer.web.WebVolativeSurfaceManager \
           java.awt.event.MouseEvent \

$(JNI_LIST):
	$(JAVAH) -force -classpath $(CLASSPATH) -d $(TARGET_DIR) $(JNI_LIST)

all: $(JNI_LIST) init $(SOURCES) $(EXECUTABLE)

.PHONY:
init:
	$(COPY) $(SOURCES) $(TARGET)

$(EXECUTABLE): $(OBJECTS)
	$(CC) $(OBJECTS) -o $(TARGET_DIR)/$@ $(MYLDFLAGS) $(LDFLAGS)

.c.o:
	$(CC) $(MYCFLAGS) $(CFLAGS) $(INCLUDE) $< -o $@

clean-lib:
	rm $(TARGET_DIR)/$(EXECUTABLE)

clean-obj:
	rm $(OBJECTS) $(TARGET)

clean: clean-obj clean-lib
