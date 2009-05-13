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
OBJECTDIR=build/Release/${PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11VolatileSurfaceManager.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11Blit.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11SurfaceData.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11GraphicsEnvironment.o \
	${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11PlatformScreen.o

# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	${MAKE}  -f nbproject/Makefile-Release.mk dist/Release/${PLATFORM}/libnative.so

dist/Release/${PLATFORM}/libnative.so: ${OBJECTFILES}
	${MKDIR} -p dist/Release/${PLATFORM}
	${LINK.c} -shared -o dist/Release/${PLATFORM}/libnative.so -fPIC ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11VolatileSurfaceManager.o: ../../src/x11/native/X11VolatileSurfaceManager.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -O2 -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11VolatileSurfaceManager.o ../../src/x11/native/X11VolatileSurfaceManager.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11Blit.o: ../../src/x11/native/X11Blit.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -O2 -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11Blit.o ../../src/x11/native/X11Blit.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11SurfaceData.o: ../../src/x11/native/X11SurfaceData.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -O2 -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11SurfaceData.o ../../src/x11/native/X11SurfaceData.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11GraphicsEnvironment.o: ../../src/x11/native/X11GraphicsEnvironment.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -O2 -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11GraphicsEnvironment.o ../../src/x11/native/X11GraphicsEnvironment.c

${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11PlatformScreen.o: ../../src/x11/native/X11PlatformScreen.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native
	${RM} $@.d
	$(COMPILE.c) -O2 -I../../build/x11/include -I${openjdk.build}/include -I${openjdk.build}/include/linux -I${openjdk.src}/jdk/src/share/native/sun/java2d -I../../../openjdk/openjdk7/build/linux-amd64/include -I../../../openjdk/openjdk7/build/linux-amd64/include/linux -fPIC  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/home/roman/src/hg/caciocavallo-ng/netbeans/native/../../src/x11/native/X11PlatformScreen.o ../../src/x11/native/X11PlatformScreen.c

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf:
	${RM} -r build/Release
	${RM} dist/Release/${PLATFORM}/libnative.so

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
