#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=
CXX=
FC=

# Macros
PLATFORM=GNU-Linux-x86

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=build/Debug/${PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11GraphicsEnvironment.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11PlatformWindow.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11VolatileSurfaceManager.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native/SDLScreen.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native/SDLSurfaceData.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11Blit.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11SurfaceData.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11PlatformScreen.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native/SDLGraphicsEnvironment.o

# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-lX11

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	${MAKE}  -f nbproject/Makefile-Debug.mk dist/Debug/${PLATFORM}/libcacio-x11.so

dist/Debug/${PLATFORM}/libcacio-x11.so: ${OBJECTFILES}
	${MKDIR} -p dist/Debug/${PLATFORM}
	${LINK.c} -shared -o dist/Debug/${PLATFORM}/libcacio-x11.so -fPIC ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11GraphicsEnvironment.o: ../../src/x11/native/X11GraphicsEnvironment.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -g -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I../../../openjdk/jdk/src/share/native/common/ -I../../../openjdk/build/linux-i586-debug/tmp/sun/sun.awt/awt/CClassHeaders -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11GraphicsEnvironment.o ../../src/x11/native/X11GraphicsEnvironment.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11PlatformWindow.o: ../../src/x11/native/X11PlatformWindow.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -g -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I../../../openjdk/jdk/src/share/native/common/ -I../../../openjdk/build/linux-i586-debug/tmp/sun/sun.awt/awt/CClassHeaders -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11PlatformWindow.o ../../src/x11/native/X11PlatformWindow.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11VolatileSurfaceManager.o: ../../src/x11/native/X11VolatileSurfaceManager.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -g -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I../../../openjdk/jdk/src/share/native/common/ -I../../../openjdk/build/linux-i586-debug/tmp/sun/sun.awt/awt/CClassHeaders -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11VolatileSurfaceManager.o ../../src/x11/native/X11VolatileSurfaceManager.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native/SDLScreen.o: ../../src/sdl/native/SDLScreen.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native
	${RM} $@.d
	$(COMPILE.c) -g -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I../../../openjdk/jdk/src/share/native/common/ -I../../../openjdk/build/linux-i586-debug/tmp/sun/sun.awt/awt/CClassHeaders -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native/SDLScreen.o ../../src/sdl/native/SDLScreen.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native/SDLSurfaceData.o: ../../src/sdl/native/SDLSurfaceData.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native
	${RM} $@.d
	$(COMPILE.c) -g -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I../../../openjdk/jdk/src/share/native/common/ -I../../../openjdk/build/linux-i586-debug/tmp/sun/sun.awt/awt/CClassHeaders -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native/SDLSurfaceData.o ../../src/sdl/native/SDLSurfaceData.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11Blit.o: ../../src/x11/native/X11Blit.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -g -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I../../../openjdk/jdk/src/share/native/common/ -I../../../openjdk/build/linux-i586-debug/tmp/sun/sun.awt/awt/CClassHeaders -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11Blit.o ../../src/x11/native/X11Blit.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11SurfaceData.o: ../../src/x11/native/X11SurfaceData.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -g -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I../../../openjdk/jdk/src/share/native/common/ -I../../../openjdk/build/linux-i586-debug/tmp/sun/sun.awt/awt/CClassHeaders -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11SurfaceData.o ../../src/x11/native/X11SurfaceData.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11PlatformScreen.o: ../../src/x11/native/X11PlatformScreen.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -g -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I../../../openjdk/jdk/src/share/native/common/ -I../../../openjdk/build/linux-i586-debug/tmp/sun/sun.awt/awt/CClassHeaders -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11PlatformScreen.o ../../src/x11/native/X11PlatformScreen.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native/SDLGraphicsEnvironment.o: ../../src/sdl/native/SDLGraphicsEnvironment.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native
	${RM} $@.d
	$(COMPILE.c) -g -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -I../../../openjdk/jdk/src/share/native/common/ -I../../../openjdk/build/linux-i586-debug/tmp/sun/sun.awt/awt/CClassHeaders -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/sdl/native/SDLGraphicsEnvironment.o ../../src/sdl/native/SDLGraphicsEnvironment.c

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf:
	${RM} -r build/Debug
	${RM} dist/Debug/${PLATFORM}/libcacio-x11.so

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
